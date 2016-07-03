// Global constants
var IFRAME_DEFAULT_BODY = '<!doctype html><html><head><link rel="stylesheet" type="text/css" href="style/dragdrop.css"></head><body></body></html>';
var HIGHLIGHT_CLASS = 'highlight'; 
var DATA_ATTR_NAME = 'customData';
var IFRAME_ID_NAME = 'childFrame'
var NR_DRAG_ELEMENTS = 2;
var LOCAL_SERVER_URL = "http://localhost:10093/test.json";

// iFrame creation function 
jQuery.fn.extend({
	iframe: function(){
		var iFrameDocument = this.contents()[0];
		iFrameDocument.open();
		iFrameDocument.write(IFRAME_DEFAULT_BODY);
		iFrameDocument.close();
		return this;
	}
});

// Server requestor module
(function($) {
	$.serverRequestor = function( ) {
		var sReq = {
			corsError: 'CORS not supported',
			responseErrorMessage: 'Got no response from server',
			
			createCORSRequest: function(method, url) {
			  var xhr = new XMLHttpRequest();
			  if ("withCredentials" in xhr) {
				// XHR for Chrome/Firefox/Opera/Safari.
				xhr.open(method, url, true);
			  } else if (typeof XDomainRequest != "undefined") {
				// XDomainRequest for IE.
				xhr = new XDomainRequest();
				xhr.open(method, url);
			  } else {
				// CORS not supported.
				xhr = null;
			  }
			  return xhr;
			},
			
			getServerMessage: function(url, printFunc) {
			  // Attempt to make CORS request
			  var xhr = this.createCORSRequest('GET', url); 
			  if (!xhr) {
				console.log(this.corsError);
				return;
			  }

			  // Make request
			  xhr.onload = function( ) {
				var text = xhr.responseText;
				if(!text) {
					printFunc(this.responseErrorMessage);
				}
				printFunc(JSON.parse(text).message); 
			 }
			  xhr.send();	
			}
			};
			
			return {
				getServerMessage: sReq.getServerMessage,
				createCORSRequest: sReq.createCORSRequest
			};
	};
})(jQuery);


function createInitElements() {
	for(i=1; i<=NR_DRAG_ELEMENTS; i++) {
		$('<div></div>').attr({ customData : "property" + i , "id": "Element" + i}).addClass("DragElement").text("Element " + i).appendTo($("body"));
	}
	
	$('<iframe id="childFrame"></iframe>').appendTo('body').iframe();
	
	$('#childFrame').contents().find('body').empty().append("<div id='DroppableDiv1' class='DroppableElement'></div>").show();
}

function configureDraggableElements(dragClass) {
	 $(dragClass).draggable({
		revert: "invalid",
		iframeFix: true,
		helper: function() {
		  var highlighted = $('.DragElement.' + HIGHLIGHT_CLASS);
		  console.log("Highlighted elements (" + highlighted.length + ")" );
		  if (highlighted.length === 0) {
			highlighted = $(this);
		  }
		  
		  var container = $('<div/>').attr('id', 'draggingContainer');
		  container.append(highlighted.clone());
		  return container;
		}
	  });

	$(dragClass).click( function(event) {
		event.preventDefault();
		if(event.ctrlKey)  {
			$(this).toggleClass(HIGHLIGHT_CLASS);
		}
	});
}

function configureDrappableArea(droppableClass) {
		$("#childFrame").contents().find(droppableClass).droppable({ 
		hoverClass: "ui-state-active",
		iframeFix: true,
		tolerance: 'pointer',
		drop: function (event, ui) {
				var sReq = $.serverRequestor();
				sReq.getServerMessage(LOCAL_SERVER_URL, function(response) { console.log("Server message = " + response) } ); 
				
				console.log("Custom data attribute values:");
				$.each(ui.helper.children(), function(index, value) {
				  console.log(value.getAttribute(DATA_ATTR_NAME));
				});
				
				$(this).append(ui.helper.children());
			}
		});
}

function createDragDropEnvironment() {
	createInitElements();
	configureDraggableElements('.DragElement');
	configureDrappableArea('.DroppableElement');  
}

$(document).ready($(createDragDropEnvironment));
		