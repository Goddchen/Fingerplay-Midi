$(document).ready(function(){
    
    $('.editor1 .editor').dragResize({grid:30});

    $('.boton').not("#render").click(function(){
        var tipo = $(this).attr("id");
        
        if(tipo=="btn"){
            var width = 60,
                height = 60;
        }
        else if(tipo=="slr"){
            var width = 60,
                height = 120;
        }
        else if(tipo=="pad"){
            var width = 120,
                height = 120;
        }

        var div = $("<div/>",{
            class: 'block snap-to-grid ' + tipo,
            style: 'width: ' + width + 'px; height: ' + height + 'px; top: ' + (300 - height) + 'px; left: ' + (420 - width) + 'px;',
            "data-type": tipo
        }).append('<div class="handle"/>').append('<div class="resize"/>')

        $(".editor").append(div);
    })



    $("#render").click(function(){
        var xw = new XMLWriter('UTF-8'),
            controllerN = 1;

        xw.formatting = 'indented';//add indentation and newlines
        xw.indentChar = '    ';//indent with spaces
        xw.indentation = 1;//add 2 spaces per level

        xw.writeStartDocument( );
        //xw.writeStartElement( 'layouts' );
        //xw.writeAttributeString( 'version', '2');
            
            xw.writeStartElement('layout');
            xw.writeAttributeString( 'screenWidth', '480');
            xw.writeAttributeString( 'screenHeight', '320');

                xw.writeStartElement('screen');
                xw.writeAttributeString( 'x', '0');
                xw.writeAttributeString( 'y', '0');
                xw.writeAttributeString( 'width', "420");
                xw.writeAttributeString( 'height', "300");

                    $(".editor .block").each(function(i){
                        var elem = $(this),
                            $type = elem.attr("data-type"),
                            $width = elem.css("width").slice(0, -2),
                            $height = elem.css("height").slice(0, -2),
                            $x = elem.css("left").slice(0, -2),
                            $y = (-240 + parseInt(elem.css("top").slice(0, -2) ) ) * -1;

                        if($type == "btn")
                            xw.writeStartElement('pad');
                        else if($type == "slr")
                            xw.writeStartElement('slider');
                        else if($type == "pad")
                            xw.writeStartElement('xypad');
                        else return false;

                        xw.writeAttributeString( 'x', $x);
                        xw.writeAttributeString( 'y', $y);
                        xw.writeAttributeString( 'width', $width);
                        xw.writeAttributeString( 'height', $height);

                        xw.writeAttributeString( 'controllerNumber', controllerN);
                        if($type == "pad")controllerN += 3;
                        else if($type == "slr")controllerN += 2;
                        else if($type == "btn")controllerN += 1;

                        xw.writeEndElement();

                    });

                xw.writeEndElement();

            xw.writeEndElement();

        //xw.writeEndElement();
        xw.writeEndDocument();

        $(".result").text(xw.flush());
        xw.close();
    });

});
