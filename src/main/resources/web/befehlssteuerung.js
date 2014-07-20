$(function() {


    var l = document.liste;
    $("#bereich").html("");

    for (var i = 0; i < l.length; i++) { // liste aus der allgemeinen jsbin-definition
        $("#bereich").append("<input id='liste_" + i + "' type='button' value='" + l[i].name + "' class='befehl' />");

    }

    $(".befehl").on("click", function() {
        var nummer = parseInt(($(this).attr("id").split("_"))[1]);
        $("#beschreibung").html(l[nummer].beschreibung);
        eb.publish("showserver.alle", {message: 'aktion#neueseite#/lade/' + l[nummer].name + '/' + l[nummer].adresse});
    });


    var eb = new vertx.EventBus('/bridge');

    eb.onopen = function() {

        eb.registerHandler('showserver.steuerung', function(message) {

            var teile = message.message.split("#");
            if (teile.length > 0) {
                if (teile[0] === "bedienelemente") {
                    var anzahl = parseInt(teile[1]);
                    var htmltext = "<table>";
                    htmltext += "<tr><td><input type='button'  class='knopf' id='toggle' value='Bedienelemente' /></td><td>Schaltet Bedienelemente ein bzw. aus</td></tr>";
                    for (var i = 0; i < anzahl; i++) {                    		 	 //$("#befehle").append(teile[2+i]);
                        var bereich = teile[2 + i];
                        var teil = $(bereich); // zur analyse mit jquery
                        var beschreibung = teil.attr("b");
                        htmltext += "<tr><td>" + teile[2 + i] + "</td><td>" + beschreibung + "</td></tr>";


                    }

                    htmltext += "</table>";

                    $("#befehle").html(htmltext);
                    $(".knopf").on("click", function() {
                        eb.publish("showserver.alle", {message: 'aktion#click#' + $(this).attr("id")});

                    });
                    $(".select").on("change", function() {
                        var wert = $("#auswahl option:selected").val();
                        eb.publish("showserver.alle", {message: 'aktion#change#' + wert + "#" + $(this).attr("id")});

                    });
                }
            }

        });





    };
});