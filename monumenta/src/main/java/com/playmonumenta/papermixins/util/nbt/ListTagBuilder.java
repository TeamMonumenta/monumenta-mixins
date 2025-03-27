package com.playmonumenta.papermixins.util.nbt;

import java.util.Collections;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

public class ListTagBuilder implements Supplier<Tag> {
	private final ListTag tag = new ListTag();

	private ListTagBuilder() {
	}

	public static ListTagBuilder of() {
		return new ListTagBuilder();
	}

	public static ListTagBuilder of(Tag... tags) {
		return new ListTagBuilder().put(tags);
	}

	public static ListTagBuilder of(Supplier<Tag> tag) {
		return new ListTagBuilder().put(tag.get());
	}

	public static ListTag build(Tag tag) {
		return new ListTagBuilder().put(tag).get();
	}

	public static ListTag build(Supplier<Tag> tag) {
		return new ListTagBuilder().put(tag.get()).get();
	}

	public static ListTag of(Stream<Tag> tags) {
		final var instance = new ListTagBuilder();
		tags.forEach(instance::put);
		return instance.get();
	}

	public ListTagBuilder put(Tag... tags) {
		Collections.addAll(tag, tags);
		return this;
	}

	@Override
	public ListTag get() {
		return tag;
	}
}
