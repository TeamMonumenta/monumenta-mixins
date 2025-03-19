package com.playmonumenta.mixinapi.v1.hook;

import de.tr7zw.nbtapi.iface.ReadWriteNBT;

public interface Persistent<A> {
	void serialize(ReadWriteNBT nbt, A attachment);

	void deserialize(ReadWriteNBT nbt, A attachment);
}
