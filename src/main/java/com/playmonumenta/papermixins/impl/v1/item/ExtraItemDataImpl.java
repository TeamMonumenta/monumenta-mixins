package com.playmonumenta.papermixins.impl.v1.item;

import com.google.common.base.Preconditions;
import com.playmonumenta.mixinapi.v1.item.ExtraItemData;
import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;

public class ExtraItemDataImpl implements ExtraItemData {
	private boolean unbreakable = false;
	private List<Component> text = null;
	private BookMeta.Generation generation = null;
	private String author = null;
	private Component title = null;
	private NBTContainer extraTag;

	public Component title() {
		return title;
	}

	public BookMeta.Generation generation() {
		return generation;
	}

	public List<Component> bookPages() {
		return text;
	}

	public String author() {
		return author;
	}

	public boolean isUnbreakable() {
		return unbreakable;
	}

	@Override
	public void setUnbreakable(boolean unbreakable) {
		this.unbreakable = unbreakable;
	}

	@Override
	public void setBookPages(@NotNull List<Component> pages) {
		Preconditions.checkNotNull(pages);

		this.text = new ArrayList<>(pages);
	}

	@Override
	public void setBookGeneration(BookMeta.@NotNull Generation generation) {
		Preconditions.checkNotNull(generation);
		this.generation = generation;
	}

	@Override
	public void setBookAuthor(@NotNull String author) {
		Preconditions.checkNotNull(author);
		this.author = author;
	}

	@Override
	public void setBookTitle(@NotNull Component title) {
		Preconditions.checkNotNull(title);
		this.title = title;
	}

	@Override
	public ReadWriteNBT getNBTTag() {
		if (extraTag == null) {
			extraTag = new NBTContainer();
		}

		return extraTag;
	}
}
