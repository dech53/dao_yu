package com.dech53.dao_yu.static

data class ForumCategory(
    val name: String,
    val id: String,
    val forums: List<ForumItem>
)

data class ForumItem(
    val id: String,
    val name: String,
//    val clickAction:()->Unit
)

val forumCategories = listOf(
    ForumCategory(
        name = "时间线",
        id = "999",
        forums = listOf(
            ForumItem(id = "1", name = "综合线"),
            ForumItem(id = "2", name = "创作线"),
            ForumItem(id = "3", name = "非创作线"),
        )
    ),
    ForumCategory(
        name = "亚文化",
        id = "1",
        forums = listOf(
            ForumItem(id = "53", name = "婆罗门一"),
            ForumItem(id = "12", name = "漫画"),
            ForumItem(id = "14", name = "动画综合"),
            ForumItem(id = "31", name = "影视"),
            ForumItem(id = "116", name = "主播管人"),
            ForumItem(id = "45", name = "卡牌桌游"),
            ForumItem(id = "9", name = "特摄"),
            ForumItem(id = "102", name = "战锤"),
            ForumItem(id = "39", name = "胶佬"),
            ForumItem(id = "94", name = "铁道厨"),
            ForumItem(id = "6", name = "VOCALOID"),
            ForumItem(id = "90", name = "小马"),
            ForumItem(id = "5", name = "东方Project"),
            ForumItem(id = "93", name = "舰娘")
        )
    ),
    ForumCategory(
        name = "综合",
        id = "4",
        forums = listOf(
            ForumItem(id = "4", name = "综合版1"),
            ForumItem(id = "98", name = "DANGER_U"),
            ForumItem(id = "20", name = "欢乐恶搞"),
            ForumItem(id = "121", name = "速报2"),
            ForumItem(id = "17", name = "绘画"),
            ForumItem(id = "110", name = "社畜"),
            ForumItem(id = "19", name = "故事"),
            ForumItem(id = "81", name = "都市怪谈"),
            ForumItem(id = "37", name = "军武"),
            ForumItem(id = "30", name = "技术宅"),
            ForumItem(id = "75", name = "数码"),
            ForumItem(id = "118", name = "宠物"),
            ForumItem(id = "97", name = "女装2"),
            ForumItem(id = "106", name = "买买买")
        )
    ),
    ForumCategory(
        name = "创作",
        id = "5",
        forums = listOf(
            ForumItem(id = "111", name = "跑团"),
            ForumItem(id = "57", name = "创作茶水间"),
            ForumItem(id = "91", name = "规则怪谈"),
            ForumItem(id = "11", name = "海龟汤"),
            ForumItem(id = "15", name = "科学"),
            ForumItem(id = "103", name = "文学"),
            ForumItem(id = "35", name = "音乐"),
            ForumItem(id = "27", name = "AI"),
            ForumItem(id = "115", name = "摄影"),
            ForumItem(id = "112", name = "ROLL点")
        )
    ),
    ForumCategory(
        name = "游戏",
        id = "3",
        forums = listOf(
            ForumItem(id = "2", name = "游戏综合"),
            ForumItem(id = "3", name = "手游专楼"),
            ForumItem(id = "25", name = "任天堂"),
            ForumItem(id = "22", name = "腾讯游戏"),
            ForumItem(id = "23", name = "暴雪游戏"),
            ForumItem(id = "124", name = "SE"),
            ForumItem(id = "70", name = "V社"),
            ForumItem(id = "28", name = "怪物猎人"),
            ForumItem(id = "68", name = "鹰角游戏"),
            ForumItem(id = "47", name = "米哈游"),
            ForumItem(id = "34", name = "音游打卡"),
            ForumItem(id = "10", name = "联机")
        )
    ),
    ForumCategory(
        name = "生活",
        id = "8",
        forums = listOf(
            ForumItem(id = "62", name = "露营"),
            ForumItem(id = "113", name = "育儿"),
            ForumItem(id = "120", name = "自救互助"),
            ForumItem(id = "32", name = "料理"),
            ForumItem(id = "33", name = "体育"),
            ForumItem(id = "56", name = "学业打卡"),
            ForumItem(id = "89", name = "日记")
        )
    ),
    ForumCategory(
        name = "管理",
        id = "6",
        forums = listOf(
            ForumItem(id = "18", name = "值班室"),
            ForumItem(id = "117", name = "技术支持"),
            ForumItem(id = "96", name = "版务"),
            ForumItem(id = "60", name = "百脑汇")
        )
    )
)