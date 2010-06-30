function callWicketEventGet(wicketUrl, wicketArgs, wicketEventType){
    var finalUrl = wicketUrl + wicketArgs + "&EVENT_TYPE=" +wicketEventType;
    wicketAjaxGet(finalUrl , function() { }, function() { });
}

function callWicketEventPost(wicketUrl, body, wicketEventType){
    var finalUrl = wicketUrl + wicketArgs + "&EVENT_TYPE=" +wicketEventType;
    wicketAjaxGet(finalUrl , function() { }, function() { });
}

function getEventArguments(event){
    currentEventString = jsonToArgs(event,"Event");
    currentEventString += "&Event_GEN_start=" + event.start.getTime();
    if (event.end != null){
        currentEventString += "&Event_GEN_end=" + event.end.getTime();
    }
    return currentEventString;
}

function jsonToArgs(jsonObject,baseKey){
    var currentArgString = "";
    for(var key in jsonObject){
        currentArgString += "&" +baseKey +"_" +key + "="+ jsonObject[key];
    }
    return currentArgString;
}


function callWicketEventDrop(wicketUrl,event,dayDelta,minuteDelta,allDay,revertFunc){
    var eventDropArgs = getEventArguments(event) +
    "&dayDelta=" + dayDelta + "&minuteDelta=" + minuteDelta +
    "&allDay=" + allDay + "&revertFunc=" + revertFunc ;
    callWicketEventGet(wicketUrl, eventDropArgs , "eventDrop");
}

function callWicketEventClick(wicketUrl,calEvent, jsEvent, view) {
    var eventClickArgs =getEventArguments(calEvent);
    callWicketEventGet(wicketUrl, eventClickArgs , "eventClick");
}


//
// Util Method to Dump Json
//
function DumpObjectIndented(obj, indent)
{
    var result = "";
    if (indent == null) indent = "";

    for (var property in obj)
    {
        var value = obj[property];
        if (typeof value == 'string')
            value = "'" + value + "'";
        else if (typeof value == 'object')
        {
            if (value instanceof Array)
            {
                // Just let JS convert the Array to a string!
                value = "[ " + value + " ]";
            }
            else
            {
                // Recursive dump
                // (replace "  " by "\t" or something else if you prefer)
                var od = DumpObjectIndented(value, indent + "  ");
                // If you like { on the same line as the key
                //value = "{\n" + od + "\n" + indent + "}";
                // If you prefer { and } to be aligned
                value = "\n" + indent + "{\n" + od + "\n" + indent + "}";
            }
        }
        result += indent + "'" + property + "' : " + value + ",\n";
    }
    return result.replace(/,\n$/, "");
}