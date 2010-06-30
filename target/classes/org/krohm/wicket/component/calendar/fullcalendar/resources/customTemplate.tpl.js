//window.onload = function () {
 $(document).ready(function() {
   // do stuff when DOM is ready

    
    $("#${markupId}").fullCalendar({

        editable: true,
        header: {
            left: 'prev,next today',
            center: 'title',
            right: 'month,agendaWeek,agendaDay'
        },
        
        events: function(start, end, callback) {
            $.getJSON("${getListBehaviourUrl}" + "&start=" + start.getTime() +"&end=" + end.getTime(),
                function(data){
                    callback(data);
                });
        },

        eventDrop: function(event,dayDelta,minuteDelta,allDay,revertFunc) {
            callWicketEventDrop("${eventBehaviourUrl}",event,dayDelta,minuteDelta,allDay,revertFunc);
        },

        eventClick: function(calEvent, jsEvent, view) {
            callWicketEventClick("${eventBehaviourUrl}",calEvent, jsEvent, view);
        }
        
    });
//};
 });
