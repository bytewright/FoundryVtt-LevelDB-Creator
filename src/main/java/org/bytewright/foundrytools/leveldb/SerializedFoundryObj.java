package org.bytewright.foundrytools.leveldb;

import org.bytewright.foundrytools.json.pojo.BaseFoundryVttObject;

public record SerializedFoundryObj(BaseFoundryVttObject foundryObject, String json) {
}
