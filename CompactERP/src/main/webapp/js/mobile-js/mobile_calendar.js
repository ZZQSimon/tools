/**
 * Created by Administrator on 2017/8/14.
 */

registerInit('mobile_calendar', function (form) {
	var $form=form.get();
	
	$form.find(".calendar-container").workCalendar({
        ifSwitch: true, // 是否切换月份
        hoverDate: true, // hover是否显示当天信息
        backToday: true ,// 是否返回当天
        form:form,		//dx from object
        initEventTag:initEventTag,	//	初始化事件标记
        initCalendarRule:initCalendarRule,	//	初始化日历规则
        initEventList:initEventList//  初始化事件列表
    });


	function initEventTag(form,month){
    	var $form=form.get();
    	month=month.length>1?month:"0"+month;
		postJson('/calendar/initEventTag.do',{month:month},function(result){
			var eventTags=result.eventTags;
			var cur;
			var $li;
			var begin;
			var end;
			$form.find(".event-item").remove();
			for(var key in eventTags){
				begin=new Date(new Date(eventTags[key].begin_date).Format("yyyy-MM-dd")+" 00:00").getTime();
				end=new Date(new Date(eventTags[key].end_date).Format("yyyy-MM-dd") +" 00:00").getTime();
				
				if(begin==end){
					$form.find("[data='"+new Date(begin).Format("yyyyMMdd")+"']").append("<div class='event-item' begin='"+eventTags[key].begin_date+"' end='"+eventTags[key].end_date+"' content='"+eventTags[key].content+"'>"+eventTags[key].event_name+"</div>");
				}else{
					cur=begin;
					while(cur<=end){
					 	$li=$form.find("[data='"+new Date(cur).Format("yyyyMMdd")+"']");
						
					 	if(cur==begin)
							$li.append("<div class='event-item begin-time' begin="+eventTags[key].begin_date+" end="+(begin+(24*60*59-1)*1000)+" content='"+eventTags[key].content+"'>"+eventTags[key].event_name+"</div>");
						else if(cur==end) 
							$li.append("<div class='event-item middle-time' begin="+cur+" end="+eventTags[key].end_date+" content='"+eventTags[key].content+">"+eventTags[key].event_name+"</div>");
						else 
							$li.append("<div class='event-item end-time' begin="+cur+" end="+(cur+(24*60*59-1)*1000)+" content='"+eventTags[key].content+">"+eventTags[key].event_name+"</div>");
						
						cur+=86400000;
					}
				}
			}
		});
	}
	
	function initCalendarRule(form,month){
		postJson('/calendar/getRestRule.do',{month:month},function(result){
			var commonRule=result.commonRule;
			var specialRule=result.specialRule;
			var list=['monday','tuesday','wednesday','thursday','friday','saturday','sunday'];
			var css;
			var data;
			var $li;
			var $form=form.get();
			for(var i=0;i<list.length;i++){
				css=commonRule[list[i]]==1?"workday":"holiday";
				$form.find("li[name='"+list[i]+"']").addClass(css);
			}
			for(var key in specialRule){
				data=specialRule[key].date.replace(/-/g,"");
				css=specialRule[key].is_holiday==1?"holiday":"workday";
				$li=$form.find("li[data='"+data+"']");
				$li.removeClass("workday");
				$li.removeClass("holiday");
				$li.addClass(css);
			}
		});
	}
	
	function initEventList(form,date){
		var $form=form.get();
		var begintime;
		var endtime;
		var content;
		var $event=$form.find(".event-list");
		date=new Date(date).Format('yyyyMMdd');
		$event.empty();
		$form.find('[data=' + date + ']').find(".event-item").each(function(){
			begintime=new Date(parseInt($(this).attr("begin"))).Format("hh:mm");
			endtime=new Date(parseInt($(this).attr("end"))).Format("hh:mm")
			content=$(this).attr("content");
			content=isEmpty(content)?"":content;
			$event.append($('<div class="event-item"><div class="event-point"></div><div class="event-time"><p>'+begintime+'</p><p>'+endtime+'</p></div><div class="event-content">'+content+'</div></div>'));
		});
	}
});
