



$(function() {
    var text = "";
    var elemente = [];
    var zuHolen = $(".holen");
    $.each(zuHolen, function(index, value) {

        elemente.push(zuHolen[index].outerHTML);
    }
    );

    function objekt() {
        this.typ = "bedienelemente";
        this.laenge = 0;
        this.elemente = [];
    }

    var zuSenden = new objekt();
    var dummy = "bedienelemente#" + elemente.length;

    if (elemente.length > 0) {

        for (var i = 0; i < elemente.length; i++) {
            dummy += "#" + elemente[i];
        }

    }

    text = dummy;

    var eb = new vertx.EventBus('/bridge');

    eb.onopen = function() {

        eb.registerHandler('showserver.alle', function(message) {

            console.log('received a message: ' + JSON.stringify(message));
            var teile = message.message.split("#");
            if (teile.length > 0) {
                if (teile[0] === "aktion") {
                    if (teile[1] === "click") { // knopf gedrÃ¼ckt
                        if (teile[2] === 'toggle') {
                            $(".holen").toggle();
                        } else {
                            $("#" + teile[2]).trigger(teile[1]);
                        }
                    } else if (teile[1] === "change") { // auswahl geÃ¤ndert
                        $("#" + teile[3]).val(teile[2]).change();
                    } else if (teile[1] === "neueseite") {
                        window.location.href = teile[2];
                    }

                }
            }

        });

        eb.send('showserver.steuerung', {befehl: "neueElemente", message: text});

    }
});