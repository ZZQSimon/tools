/**
 * Created by Administrator on 2018/6/26.
 */
function g1WidgetS4() {
    return (((1+Math.random())*0x10000)|0).toString(16).substring(1);
}
function g1WidgetGuid() {
    return (g1WidgetS4()+g1WidgetS4()+"-"+g1WidgetS4()+"-"+g1WidgetS4()+"-"+g1WidgetS4()+"-"+g1WidgetS4()+g1WidgetS4()+g1WidgetS4());
}
