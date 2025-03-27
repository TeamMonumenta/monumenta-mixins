package com.playmonumenta.papermixins.util.nbt;

import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public class CompoundTagBuilder implements Supplier<Tag> {
	private final CompoundTag tag = new CompoundTag();

	private CompoundTagBuilder() {
	}

	public static CompoundTagBuilder of() {
		return new CompoundTagBuilder();
	}

	public static CompoundTagBuilder of(String key, Tag value) {
		return new CompoundTagBuilder().put(key, value);
	}

	public static CompoundTagBuilder of(String key, Supplier<Tag> value) {
		return new CompoundTagBuilder().put(key, value.get());
	}

	public static CompoundTag build(String key, Tag value) {
		return new CompoundTagBuilder().put(key, value).get();
	}

	public static CompoundTag build(String key, Supplier<Tag> value) {
		return new CompoundTagBuilder().put(key, value.get()).get();
	}

	public CompoundTagBuilder put(String key, Supplier<Tag> value) {
		tag.put(key, value.get());
		return this;
	}

	public CompoundTagBuilder put(String key, Tag value) {
		tag.put(key, value);
		return this;
	}

	public CompoundTagBuilder put(String key, String value) {
		tag.putString(key, value);
		return this;
	}

	public CompoundTagBuilder put(String key, int value) {
		tag.putInt(key, value);
		return this;
	}

	public CompoundTagBuilder put(String key, byte value) {
		tag.putByte(key, value);
		return this;
	}

	@Override
	public CompoundTag get() {
		return tag;
	}
}
