package com.order.app.data.preset

object PresetRecipes {
    val cocktails = listOf(
        Cocktail(
            name = "Mojito",
            category = "cocktail",
            price = 48.0,
            ingredients = listOf(
                "白朗姆酒" to "45ml", "青柠汁" to "20ml", "薄荷叶" to "8-10片",
                "细砂糖" to "2茶匙", "苏打水" to "适量"
            ),
            steps = listOf(
                "在杯中放入薄荷叶和细砂糖", "用捣棒轻轻按压薄荷叶",
                "倒入青柠汁和朗姆酒", "加满碎冰", "倒入苏打水至满杯", "用薄荷枝装饰"
            )
        ),
        Cocktail(
            name = "Margarita",
            category = "cocktail",
            price = 52.0,
            ingredients = listOf("龙舌兰酒" to "50ml", "君度橙酒" to "25ml", "青柠汁" to "25ml", "盐" to "适量（杯口）"),
            steps = listOf("用青柠片擦拭杯口", "将杯口蘸盐", "将龙舌兰酒、君度橙酒和青柠汁倒入摇壶", "加冰用力摇晃15秒", "过滤倒入预冷的玛格丽特杯")
        ),
        Cocktail(
            name = "Old Fashioned",
            category = "cocktail",
            price = 55.0,
            ingredients = listOf("波本威士忌" to "60ml", "方糖" to "1块", "安格斯图拉苦精" to "2滴", "苏打水" to "少许", "橙皮" to "1片"),
            steps = listOf("在古典杯中放入方糖", "加入苦精和少许苏打水", "用捣棒将糖捣碎融化", "加入一大块冰块", "倒入波本威士忌", "轻轻搅拌", "用橙皮扭转出油后放入杯中")
        ),
        Cocktail(
            name = "Daiquiri",
            category = "cocktail",
            price = 45.0,
            ingredients = listOf("白朗姆酒" to "50ml", "青柠汁" to "25ml", "糖浆" to "15ml"),
            steps = listOf("将所有原料倒入摇壶", "加冰用力摇晃10秒", "过滤倒入预冷的鸡尾酒杯")
        ),
        Cocktail(
            name = "Martini",
            category = "cocktail",
            price = 58.0,
            ingredients = listOf("金酒" to "60ml", "干味美思" to "10ml", "橄榄" to "1颗"),
            steps = listOf("将金酒和干味美思倒入调酒杯", "加冰搅拌30秒", "过滤倒入预冷的马天尼杯", "放入橄榄装饰")
        ),
        Cocktail(
            name = "Negroni",
            category = "cocktail",
            price = 50.0,
            ingredients = listOf("金酒" to "30ml", "金巴利" to "30ml", "甜味美思" to "30ml", "橙皮" to "1片"),
            steps = listOf("在古典杯中放入冰块", "依次倒入金酒、金巴利和甜味美思", "轻轻搅拌", "用橙皮装饰")
        ),
        Cocktail(
            name = "Whiskey Sour",
            category = "cocktail",
            price = 48.0,
            ingredients = listOf("波本威士忌" to "50ml", "柠檬汁" to "25ml", "糖浆" to "15ml", "蛋清" to "15ml（可选）"),
            steps = listOf("将所有原料倒入摇壶", "加入冰块用力摇晃15秒", "过滤倒入古典杯", "加冰块", "可用柠檬片和樱桃装饰")
        ),
        Cocktail(
            name = "Cosmopolitan",
            category = "cocktail",
            price = 50.0,
            ingredients = listOf("伏特加" to "40ml", "君度橙酒" to "15ml", "蔓越莓汁" to "25ml", "青柠汁" to "10ml"),
            steps = listOf("将所有原料倒入摇壶", "加冰用力摇晃10秒", "过滤倒入预冷的马天尼杯", "用柠檬皮或橙皮装饰")
        ),
        Cocktail(
            name = "Long Island Iced Tea",
            category = "cocktail",
            price = 60.0,
            ingredients = listOf("伏特加" to "15ml", "金酒" to "15ml", "白朗姆酒" to "15ml", "龙舌兰酒" to "15ml", "君度橙酒" to "15ml", "柠檬汁" to "25ml", "糖浆" to "10ml", "可乐" to "适量"),
            steps = listOf("在高球杯中加满冰块", "依次倒入五种酒", "加入柠檬汁和糖浆", "加可乐至满杯", "轻轻搅拌", "用柠檬片装饰")
        ),
        Cocktail(
            name = "Pina Colada",
            category = "cocktail",
            price = 50.0,
            ingredients = listOf("白朗姆酒" to "50ml", "椰浆" to "30ml", "菠萝汁" to "90ml", "碎冰" to "适量"),
            steps = listOf("将所有原料放入搅拌机", "加碎冰搅打至顺滑", "倒入飓风杯", "可用菠萝块和樱桃装饰")
        ),
        Cocktail(
            name = "Blue Lagoon",
            category = "cocktail",
            price = 45.0,
            ingredients = listOf("伏特加" to "45ml", "蓝橙力娇酒" to "20ml", "柠檬汁" to "15ml", "雪碧" to "适量"),
            steps = listOf("在高球杯中加满冰块", "倒入伏特加和蓝橙力娇酒", "加入柠檬汁", "用雪碧补满", "轻轻搅拌")
        ),
        Cocktail(
            name = "Sidecar",
            category = "cocktail",
            price = 52.0,
            ingredients = listOf("白兰地" to "50ml", "君度橙酒" to "20ml", "柠檬汁" to "20ml"),
            steps = listOf("将马天尼杯口蘸糖", "将所有原料倒入摇壶", "加冰用力摇晃", "过滤倒入预冷的杯子")
        ),
        Cocktail(
            name = "White Lady",
            category = "cocktail",
            price = 48.0,
            ingredients = listOf("金酒" to "40ml", "君度橙酒" to "20ml", "柠檬汁" to "20ml"),
            steps = listOf("将所有原料倒入摇壶", "加冰用力摇晃10秒", "过滤倒入预冷的马天尼杯", "用柠檬皮装饰")
        ),
        Cocktail(
            name = "Manhattan",
            category = "cocktail",
            price = 55.0,
            ingredients = listOf("黑麦威士忌" to "60ml", "甜味美思" to "20ml", "安格斯图拉苦精" to "2滴", "樱桃" to "1颗"),
            steps = listOf("在调酒杯中加入冰块", "倒入威士忌、味美思和苦精", "搅拌30秒", "过滤倒入预冷的马天尼杯", "放入樱桃装饰")
        ),
        Cocktail(
            name = "Tom Collins",
            category = "cocktail",
            price = 42.0,
            ingredients = listOf("金酒" to "45ml", "柠檬汁" to "25ml", "糖浆" to "15ml", "苏打水" to "适量"),
            steps = listOf("在高球杯中加满冰块", "倒入金酒、柠檬汁和糖浆", "搅拌混合", "用苏打水补满", "用柠檬片和樱桃装饰")
        )
    )

    data class Cocktail(
        val name: String,
        val category: String,
        val price: Double,
        val ingredients: List<Pair<String, String>>,
        val steps: List<String>
    )

    val defaultDrinks = listOf(
        Triple("可口可乐", "drink", 10.0),
        Triple("雪碧", "drink", 10.0),
        Triple("苏打水", "drink", 8.0),
        Triple("橙汁", "drink", 15.0),
        Triple("啤酒", "drink", 18.0)
    )

    val defaultSnacks = listOf(
        Triple("薯条", "snack", 22.0),
        Triple("鸡米花", "snack", 25.0),
        Triple("洋葱圈", "snack", 20.0),
        Triple("花生", "snack", 12.0)
    )
}
