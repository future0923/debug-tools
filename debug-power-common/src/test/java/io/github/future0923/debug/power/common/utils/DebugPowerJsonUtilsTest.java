package io.github.future0923.debug.power.common.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author future0923
 */
public class DebugPowerJsonUtilsTest {

    @Test
    public void queryConvertDebugPowerJson() {
        Assertions.assertNotEquals(DebugPowerJsonUtils.queryConvertDebugPowerJson("https://www.iconfont.cn/api/common/suggest.json?q=import&type=icon&t=1715302971995&ctoken=TSzvd6zR0QMhoi2wCCSC5xNm"), "{}");
        Assertions.assertNotEquals(DebugPowerJsonUtils.queryConvertDebugPowerJson("?q=import&type=icon&t=1715302971995&ctoken=TSzvd6zR0QMhoi2wCCSC5xNm"), "{}");
        Assertions.assertNotEquals(DebugPowerJsonUtils.queryConvertDebugPowerJson("q=import&type=icon&t=1715302971995&ctoken=TSzvd6zR0QMhoi2wCCSC5xNm"), "{}");
    }

    @Test
    public void debugPowerJsonConvertJson() {
        String json = "{\n" +
                "    \"dto\": {\n" +
                "        \"type\": \"json_entity\",\n" +
                "        \"content\": {\n" +
                "            \"dealCode\": \"\",\n" +
                "            \"dealStatus\": 0,\n" +
                "            \"dealType\": 0,\n" +
                "            \"dealAmount\": \"1\",\n" +
                "            \"payMethod\": 0,\n" +
                "            \"isMortgageHouse\": 0,\n" +
                "            \"remarks\": \"\",\n" +
                "            \"houseUserId\": 0,\n" +
                "            \"guestUserId\": 0,\n" +
                "            \"propertyRightUserId\": 0,\n" +
                "            \"loansUserId\": 0,\n" +
                "            \"guestId\": 0,\n" +
                "            \"guestCode\": \"\",\n" +
                "            \"guestDetailId\": 0,\n" +
                "            \"guestDetailCode\": \"\",\n" +
                "            \"guestInfoDetail\": \"\",\n" +
                "            \"guestJson\": \"\",\n" +
                "            \"guestName\": \"\",\n" +
                "            \"guestIdCard\": \"\",\n" +
                "            \"guestAgentName\": \"\",\n" +
                "            \"guestPhones\": \"\",\n" +
                "            \"houseId\": 0,\n" +
                "            \"houseCode\": \"\",\n" +
                "            \"houseJson\": \"\",\n" +
                "            \"ownerName\": \"\",\n" +
                "            \"ownerIdCard\": \"\",\n" +
                "            \"ownerAgentName\": \"\",\n" +
                "            \"ownerPhones\": \"\",\n" +
                "            \"codeVillage\": \"\",\n" +
                "            \"villageName\": \"\",\n" +
                "            \"numBedroom\": 0,\n" +
                "            \"numLivingRoom\": 0,\n" +
                "            \"numRestRoom\": 0,\n" +
                "            \"numKitchen\": 0,\n" +
                "            \"numBalcony\": 0,\n" +
                "            \"buildArea\": \"\",\n" +
                "            \"regionName\": \"\",\n" +
                "            \"areaName\": \"\",\n" +
                "            \"direction\": 0,\n" +
                "            \"coverUrl\": \"\",\n" +
                "            \"floor\": 0,\n" +
                "            \"maxFloor\": 0,\n" +
                "            \"buildYear\": 0,\n" +
                "            \"purpose\": 0,\n" +
                "            \"handleIds\": \"\",\n" +
                "            \"handleNames\": \"\",\n" +
                "            \"handleStatus\": 0,\n" +
                "            \"handleCode\": \"\",\n" +
                "            \"handleName\": \"\",\n" +
                "            \"handleNodeCode\": \"\",\n" +
                "            \"handleNodeName\": \"\",\n" +
                "            \"incomeIds\": \"\",\n" +
                "            \"makerUserId\": 0,\n" +
                "            \"makerUserName\": \"\",\n" +
                "            \"makerDeptId\": 0,\n" +
                "            \"makerDeptName\": \"\",\n" +
                "            \"makerDate\": \"2024-05-14 08:40:39\",\n" +
                "            \"dealBankType\": 0,\n" +
                "            \"replaceDate\": \"2024-05-14 08:40:39\",\n" +
                "            \"flagArchive\": 0,\n" +
                "            \"hezId\": 0,\n" +
                "            \"renameTime\": 0,\n" +
                "            \"lendingTime\": 0,\n" +
                "            \"zfbUserId\": 0\n" +
                "        }\n" +
                "    }\n" +
                "}";
        System.out.println(DebugPowerJsonUtils.debugPowerJsonConvertJson(json));
    }

    @Test
    void debugPowerJsonConvertQuery() {
    }
}