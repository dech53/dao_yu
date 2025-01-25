package com.dech53.dao_yu.static

data class SettingsItems(
    val function: String,
    val settingsItems: List<SettingItem>
)

data class SettingItem(
    val title: String,
    val canBeChosen: Boolean?
)

var settingsItems = listOf(
    SettingsItems(
        function = "常规",
        settingsItems = listOf(
            SettingItem(
                title = "饼干管理",
                canBeChosen = false
            ),
            SettingItem(
                title = "显示精确时间",
                canBeChosen = true
            )
        )
    ),
    SettingsItems(
        function = "其他",
        settingsItems = listOf(
            SettingItem(
                title = "安利島語给朋友",
                canBeChosen = false
            ),
            SettingItem(
                title = "开源代码许可",
                canBeChosen = false
            ),
            SettingItem(
                title = "服务协议与隐私政策",
                canBeChosen = false
            ),
        )
    ),
)