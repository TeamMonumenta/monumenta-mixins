package com.playmonumenta.papermixins;

import java.util.function.Function;

public class MixinState {
	public static final ThreadLocal<Function<? super Double, Double>> IFRAME_FUNC = new ThreadLocal<>();
	public static final ThreadLocal<Double> IFRAME_VALUE = new ThreadLocal<>();
}
