package com.playmonumenta.papermixins.mcfunction.parse.reader;

import com.playmonumenta.papermixins.mcfunction.parse.ParseFeatureSet;
import java.util.List;

public record ExpandedFunctionSource(List<MCFunctionLine> codeEntries,
                                     ParseFeatureSet featureSet, List<String> rawSource) {

}
