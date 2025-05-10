package com.playmonumenta.papermixins.mixin.optimize;

import com.playmonumenta.papermixins.duck.AdvancementTreeAccess;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementTree;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Improve diagnostics for Advancement parsing
 */
@Mixin(AdvancementTree.class)
public abstract class AdvancementTreeMixin implements AdvancementTreeAccess {
	@Shadow
	@Final
	private static Logger LOGGER;

	@Shadow
	protected abstract boolean tryInsert(AdvancementHolder advancement);

	@Shadow
	@Final
	private Map<ResourceLocation, AdvancementNode> nodes;

	// TODO: we can technically optimize this faster by e bitsets and converting ResourceLocation to index
	@Unique
	private void monumenta$addAllFastImpl(List<AdvancementHolder> advancements, Map<ResourceLocation,
		AdvancementHolder> idMap) {
		// map from parent -> [all children that depend on parent]
		final var edges = new HashMap<ResourceLocation, List<AdvancementHolder>>();

		// list of all nodes with either no parents, or a parent in this.nodes
		final var roots = new ArrayList<AdvancementHolder>();

		// list of all nodes with an invalid parent
		final var invalidRoots = new HashMap<ResourceLocation, ArrayList<AdvancementHolder>>();

		for (final var advancement : advancements) {
			final var parentOptional = advancement.value().parent();

			if (advancement.value().parent().isEmpty()) {
				roots.add(advancement);
			} else {
				final var parent = parentOptional.get();

				if (idMap.containsKey(parent)) {
					edges.computeIfAbsent(parent, x -> new ArrayList<>()).add(advancement);
				} else if (nodes.containsKey(parent)) {
					roots.add(advancement);
				} else {
					invalidRoots.computeIfAbsent(advancement.value().parent().get(), r -> new ArrayList<>())
						.add(advancement);
				}
			}
		}

		// a cached BFS queue (shared across trees)
		final var queue = new ArrayDeque<AdvancementHolder>();

		// set of all loops
		// this starts off as every single element, and is progressively removed once trees are iterated
		// thus, all remaining elements are loops
		final var loopSet = new HashSet<>(advancements);

		// process all regular roots
		for (final var root : roots) {
			queue.push(root);

			while (!queue.isEmpty()) {
				final var entry = queue.pop();
				loopSet.remove(entry);

				if (!tryInsert(entry)) {
					throw new IllegalStateException("I skill issued :(");
				}

				queue.addAll(edges.getOrDefault(entry.id(), List.of()));
			}
		}

		// do the same for all invalid roots
		invalidRoots.forEach((parent, invalidRootEntries) -> {
			final var transitiveChildren = new ArrayList<AdvancementHolder>();

			for (final var root : invalidRootEntries) {
				queue.push(root);

				while (!queue.isEmpty()) {
					final var entry = queue.pop();
					loopSet.remove(entry);
					final var children = edges.getOrDefault(entry.id(), List.of());
					transitiveChildren.addAll(children);
					queue.addAll(children);
				}
			}

			// log them
			LOGGER.error(
				"Missing parent {} caused {} advancements to load: [{}] " +
					"Transitively, {} advancements failed to load: [{}]",
				parent,
				invalidRootEntries.size(),
				invalidRootEntries.stream().map(x -> x.id().toString()).collect(Collectors.joining(", ")),
				transitiveChildren.size(),
				transitiveChildren.stream().map(x -> x.id().toString()).collect(Collectors.joining(", "))
			);
		});

		// now, we process loops
		while (!loopSet.isEmpty()) {
			var value = loopSet.iterator().next();
			final var currentLoop = new ArrayList<String>();

			while (loopSet.contains(value)) {
				currentLoop.add(value.id().toString());
				loopSet.remove(value);
				value = idMap.get(value.value().parent().orElseThrow());
			}

			LOGGER.error("Cycle while loading advancements: [{}]", String.join(" -> ", currentLoop));
		}
	}

	@Inject(
		method = "addAll",
		at = @At("HEAD"),
		cancellable = true
	)
	public void useFastImpl(Collection<AdvancementHolder> advancements, CallbackInfo ci) {
		monumenta$addAllFastImpl(
			new ArrayList<>(advancements),
			advancements.stream().collect(Collectors.toMap(AdvancementHolder::id, x -> x))
		);

		ci.cancel();
	}

	@Override
	public void monumenta$addAllFast(Map<ResourceLocation, AdvancementHolder> advancements) {
		monumenta$addAllFastImpl(new ArrayList<>(advancements.values()), advancements);
	}
}
