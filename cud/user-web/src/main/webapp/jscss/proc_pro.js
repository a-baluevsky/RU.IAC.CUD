jQuery.noConflict();
var outerLayout, middleLayout;
 jQuery(document).ready(
	  function () {
        outerLayout = jQuery('body').layout(layoutSettings_Outer);
        middleLayout = jQuery('div.ui-layout-center').layout(layoutSettings);
       jQuery("#yearId").text(new Date().getFullYear());
      }
);
function setup_accordion(active_section) {
	jQuery("#accordion1").accordion({
		        fillSpace:	true,
                active: active_section,
                icons: false
                /*, event: '' */ });
}; 
function node_select(active_section, active_top_section) {
	jQuery("#link_"+active_section+"_true")
	  .css({'font-weight': 'bold', 'background-color': '#dce2e9'});
	
	jQuery("#topnav_"+active_top_section)
	 .css({'font-weight': 'bold', 'background-color': '#DADADA', 'border': '1px solid #999999'});
};
var layoutSettings = {
		 defaults: {
		           applyDefaultStyles: true
	               }
        ,north:{
                // paneSelector:	".middle-north"
                 paneSelector:	"#middle-north-id"
                ,size : Math.floor(jQuery(window).height()*(3/10))
                }
         ,center:{
	            // paneSelector: ".middle-center"
	             paneSelector: "#middle-center-id"
               }
        ,south:{
	            paneSelector:  "#middle-south-id"
	          //paneSelector:  ".middle-south"
	          ,size : Math.floor(jQuery(window).height()*(3/10))	   	
	         /* ,size : Math.floor(jQuery(window).height()*(3/8))	*/  
             /*,size: 270*/
             }
};
var layoutSettings_Outer = {
	defaults: {
		      applyDefaultStyles: true
	         ,fxName: "slide"
	         ,fxSpeed: "slow"
	         ,initClosed: false
	         }
	,north: {
		    showOverflowOnHover: true
	       ,resizable: false
	       ,spacing_open: 0
	       ,size: 40
	       ,fxName: "none"
	       ,spacing_closed: 8
	       ,togglerLength_closed:  "100%"
	       }
	,south: {
		    resizable: false
		   ,spacing_open: 0
		   ,size: 25
	       ,fxName: "none"
	       ,spacing_closed: 8
	       ,togglerLength_closed: "100%"
	       }
	,west: {
	       spacing_open: 4
	      ,size: 240
	      ,onresize: jQuery.layout.callbacks.resizePaneAccordions
		}
};