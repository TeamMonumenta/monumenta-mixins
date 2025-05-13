package com.playmonumenta.papermixins.duck;

import com.playmonumenta.papermixins.impl.v1.hook.HolderBase;

public interface HookHolderAccess<A> {
	HolderBase<A> monumenta$getHookHolder();
}
