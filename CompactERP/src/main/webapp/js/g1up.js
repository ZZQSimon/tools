$(function () {

    //左菜单初始化展开第一个模块
    $(".dx-main-menu-entry>li:first-of-type>a.nav-top-item").addClass("current");

    //滚动条插件
    $(".dx-main-menu-entry").niceScroll({cursorborder: "", cursorcolor: "#aeb2b7", cursorwidth: "2px"});
    $("#home-tab .home-sort-container,#home-tab .sort-container,#home-config-tab .tab-config-ul").niceScroll({cursorborder: "", cursorcolor: "#aeb2b7"});


    //导航标签页，，窗口变化，导航标签的变化
    $(window).resize(function () {
        $li = $(".index-tab-navs>li");
        var totalWidth = 0;
        for (var i = 0; i < $li.length; i++) {
            totalWidth = totalWidth + $li.eq(i).width() + 8;
        }

        if (dx.mainNav) {
            if (dx.mainNav.width() - 52 - totalWidth > 120) {//宽度变大了，fu->zhu
                if ($(".home-tab-navs-rest li")) {
                    var $b = $(".home-tab-navs-rest li:last-of-type");
                    dx.mainNav.append($b);
                }
            } else { //宽度变小了， zhu->fu
                $(".home-tab-navs-rest").append(dx.mainNav.find("li:last-of-type"));
            }
        }
        if ($(".home-tab-navs-rest li").length) {
            $(".home-navs-rest-drop").css("display", "block");
        } else {
            $(".home-navs-rest-drop").css("display", "none");
        }
    });

    //换主题颜色
    var cssStyle = document.getElementById('skinColour');
    function changeSyle(name) {
        event.stopPropagation();
        cssStyle.href = "/DX_STYLE/css/titleColor/" + name + ".css";
        //保存肤色名
        setStorage("skinName", name);
    }

    //html5设置本地存储
    function setStorage(sname, vul) {
        window.localStorage.setItem(sname, vul);
    }

    function getStorage(attr) {
        //var str = window.localStorage.getItem(attr);
        //return str;
        return "";
    }

    //访问本地存储，获取皮肤名
    var cssName = getStorage("skinName");
//判断是否有皮肤名，就使用获取的皮肤名，没有就用默认的
    if (cssName && cssName != null) {
        // cssStyle.href = "/DX_STYLE/js/" + cssName + ".css";
    } else {
        //没有皮肤就使用blue默认的路径
        // cssStyle.href = "Content/aps/skinColour_blue.css";
    }

    $('.usercorp').click(function () {
        changeSyle("green");
    });

    //menu click show blue
    $(".nav-node-item").on("click", function () {
        $(".nav-node-item").removeClass("openthis");
        $(this).addClass("openthis");
    });

    //展开收回左菜单
    $("#menubar").click(function () {
        if ($(this).hasClass("fa-rotate-90")) {
            $("#menubar").removeClass("fa-rotate-90");
            $('.dx-main-menu').removeClass("menuclosing");
            $('.dx-main-con').removeClass("leftMenuClosing");

        } else {
            $("#menubar").addClass("fa-rotate-90");
            $('.dx-main-menu').addClass("menuclosing");
            $('.dx-main-con').addClass("leftMenuClosing");
            $(".menuclosing .dx-main-menu-entry li ul li").off("hover");
            $(".menuclosing .dx-main-menu-entry li ul li").hover(function () {
                if($('.dx-main-con').hasClass("leftMenuClosing")){
                    $(this).children("a").addClass("current");
                    $(this).children("ul").css({"display": "block"});
                }
            }, function () {
                if($('.dx-main-con').hasClass("leftMenuClosing")){
                    $(this).children("a").removeClass("current");
                    $(this).children("ul").css({"display": "none"});
                }
            });
        }

        //表格样式重加载
        var showMenu = setTimeout(function () {
            $(".dx-main-tab-content .tab-pane.active .datagrid-f").datagrid("resize");
        },300);

    });


    //标签页导航太多时
    $(".home-navs-rest-drop ").on("click", ".home-tab-navs-rest-btn", function () {
        if ($(this).hasClass("closeDrop")) {
            $(this).parent().find(".home-tab-navs-rest").show();
            $(this).removeClass("closeDrop");
        } else {
            $(this).parent().find(".home-tab-navs-rest").hide();
            $(this).addClass("closeDrop");
        }
    });

    $(".dx-main-tabs>.index-tab-navs,.home-tab-navs-rest").on("click", "a", function () {
        if ($(this).parents("ul").hasClass("home-tab-navs-rest")) {
            $(".dx-main-tabs .index-tab-navs li,.home-tab-navs-rest>li").removeClass("active");
            $(this).addClass("active");
            $(this).parents(".dropdown").dropdown("open");
        } else {
            $(".home-tab-navs-rest>li").removeClass("active");
        }
    });



});

