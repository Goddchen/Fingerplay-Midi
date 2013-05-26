/**
* Event delgation jQuery Drag & Drop and Resize Plug-in based on EasyDrag 1.4
* Batiste Bieler : http://batiste.dosimple.ch/blog/
*
* Credits: http://fromvega.com
*/

$(function($){

    // to track if the mouse button is pressed
    var isDragMouseDown    = false;
    var isResizeMouseDown    = false;

    // to track the current element being dragged
    var currentElement = null;

    // global position records
    var lastMouseX;
    var lastMouseY;
    var lastElemTop;
    var lastElemLeft;
    var lastElemWidth;
    var lastElemHeight;

    // returns the mouse (cursor) current position
    var getMousePosition = function(e){
        if (e.pageX || e.pageY) {
            var posx = e.pageX;
            var posy = e.pageY;
        }
        else if (e.clientX || e.clientY) {
            var posx = e.clientX
            var posy = e.clientY
        }
        return { 'x': posx, 'y': posy };
    };

    var offset_snap_grip = function(grid, size) {
        var limit = grid / 2;
        if ((size % grid) > limit) {
            return grid-(size % grid);
        } else {
            return -size % grid;
        }
    }

    // updates the position of the current element being dragged
    var updatePosition = function(e, opts) {
        var pos = getMousePosition(e);

        var _left = (pos.x - lastMouseX) + lastElemLeft;
        var _top = (pos.y - lastMouseY) + lastElemTop;
        
        if(_top<0)
            _top=0;
        if(_left<0)
            _left=0;

        if($(currentElement).hasClass('snap-to-grid')) {
            _left = _left + offset_snap_grip(opts.grid, _left)
            _top = _top + offset_snap_grip(opts.grid, _top)
        }

        currentElement.style['top'] = _top + 'px';
        currentElement.style['left'] = _left + 'px';
    };

    var updateSize = function(e, opts) {
        var pos = getMousePosition(e);

        var _width = (pos.x - lastMouseX + lastElemWidth);
        var _height = (pos.y - lastMouseY + lastElemHeight);

        if(_width<50)
            _width=50;
        if(_height<50)
            _height=50;

        if($(currentElement).hasClass('snap-to-grid')){
            _width = _width + offset_snap_grip(opts.grid, _width)
            _height = _height + offset_snap_grip(opts.grid, _height)
        }

        currentElement.style['width'] = _width + 'px';
        currentElement.style['height'] = _height + 'px';
    };

    // set children of an element as draggable and resizable
    $.fn.dragResize = function(opts) {
        
        
        return this.each(function() {

            // when the mouse is moved while the mouse button is pressed
            $(this).mousemove(function(e) {
                if(isDragMouseDown) {
                    updatePosition(e, opts);
                    $(this).add($(this).parent()).addClass("move-cursor");
                    return false;
                }
                else if(isResizeMouseDown) {
                    updateSize(e, opts);
                    $(this).add($(this).parent()).addClass("resize-cursor");
                    return false;
                }
            });
    
            // when the mouse button is released
            $(this).mouseup(function(e) {
                $(this).add($(this).parent()).removeClass("resize-cursor").removeClass("move-cursor");
                isDragMouseDown = false;
                isResizeMouseDown = false;
            });

            // when an element receives a mouse press
            $(this).mousedown(function(e) {

                if($(e.target).hasClass('handle')) {

                    var el = $(e.target).parents('.block')[0];

                    isDragMouseDown = true;
                    currentElement = el;

                    // retrieve positioning properties
                    var pos = getMousePosition(e);
                    lastMouseX = pos.x;
                    lastMouseY = pos.y;

                    lastElemLeft = el.offsetLeft;
                    lastElemTop  = el.offsetTop;

                    updatePosition(e, opts);
                }

                if($(e.target).hasClass('resize')) {
                    var el = $(e.target).parents('.block')[0];

                    isResizeMouseDown = true;
                    currentElement = el;

                    var pos = getMousePosition(e);
                    lastMouseX = pos.x;
                    lastMouseY = pos.y;

                    lastElemWidth  = parseInt(el.style['width'], 10);
                    lastElemHeight = parseInt(el.style['height'], 10);

                    updateSize(e, opts);
                }
                return false;
            });
        });
    };
});