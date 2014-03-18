/*****************************************************************
Table of Contents

1.) Document Ready State
	- Nice Scroll
	- Back Top
	- Tool Tips
	- Pop Over
	- Pretty Photo
	- Sticky Menu
	- Newsletter Subscribe
	- Twitter Feeds
	- Primary Menu Responsive Button
	
2.) Window Load State
	- Latest Portfolio Carousel Widget
	- Client Carousel Widget
	- Testimonial Carousel Widget
	- Sidebar Portfolio Carousel Widget
	- Single Portfolio Carousel Widget
	- Related Project Carousel Widget
	- Twitter Carousel Widget
	- Portfolio List
	
3.) Metro Slider
	- Metro Slider Variable Setting

!Note: You can search using the title above
*****************************************************************/

/* Document Ready State. 
   Used: Global */	
jQuery(document).ready(function($)
	{
		
		/* Back Top. 
		   Used: Buttton at the corner right to scroll to the top */		
		$('#back-top').click(function(e){
				e.preventDefault();
				$('body,html').animate({scrollTop:0},800);
		});		
		
		/* Tool Tips. 
		   Used: <a class="tooltip-trigger"> */
		$('a[class^="tooltip-trigger"]').tooltip();	
		
		/* Pop Over. 
		   Used: <a class="popover-trigger"> */	
		$('a[class^="popover-trigger"]').popover();
		
		/* Pretty Photo. 
		   Used: - For Grouping:  <a data-rel="prettyPhoto[portfolio-group]"> 
		         - For Single Image: <a data-rel="prettyPhoto"> 
		*/
		$('a[data-rel]').each(function() {
			$(this).attr('rel', $(this).data('rel'));
		});
		$("a[rel^='prettyPhoto']").prettyPhoto({theme:'light_square'});		
		

		/* Sticky Menu. 
		   Used: Global */	
		var headerHeight = $("header").height();
		var logo = $("header .logo img");
		var logoSmallHeight = 60;
		var submenuHeight = $("header .vc_secondary-menu").height();

		function checkStickyMenu(){
			if($("body").hasClass("boxed")) return false;
		
			if($(window).scrollTop() > headerHeight-submenuHeight   &&  $(window).width() >= 979){
				// #Back-Top visible
				$('#back-top').addClass('visible');
				if($("body").hasClass("sticky-menu-active"))
					return false;
				$("body").addClass("sticky-menu-active","slow");
				$("body").css('padding-top',headerHeight);
				$('header').css('top',-headerHeight+40);
				$('header').stop(true, true).animate({
						top: 0
					}, 1000, function(){
						$('header').removeAttr('style');
						// Animation complete.
					});
				logo.height(logoSmallHeight).css("width", "auto");				
			} else if( $(window).scrollTop() == 0 ||  $(window).width() < 979){
				$('#back-top').removeClass('visible');
				if ($("body").hasClass("sticky-menu-active")){
					$("body").css('padding-top',0);
					$("body").removeClass("sticky-menu-active");
				}
				logo.removeAttr('style');
			}
		}
		$(window).on("scroll", function(){
				checkStickyMenu();
		});
		$(window).on("resize", function(){
				checkStickyMenu();
		});
		checkStickyMenu();



		/* Newsletter Subscribe. 
		   Used: At the footer fourth column */
		$("#newsletter").validate({
					submitHandler: function(form) {
						var action = $(this).attr('action');
						$(form).ajaxSubmit({
							type: "POST",
							url: $("#newsletter-form").attr("action"),
							data: {
								"email": $("#newsletter-form #email").val()
							},
							dataType: "json",
							success: function (data) {
								if (data.response == "success") {
		
									$("#vc_newsletter-form-success").removeClass("hidden");
									$("#vc_newsletter-form-error").addClass("hidden");
		
									$("#newsletter-form #email")
										.val("")
										.blur()
										.closest(".control-group")
										.removeClass("success")
										.removeClass("error");
									$("#vc_newsletter-form-widget .info").fadeOut(500,function(){
										$('#vc_newsletter-form-success').fadeIn(500);
									});	
		
								} else {
									$("#vc_newsletter-form-error").html(data.message);
									$("#vc_newsletter-form-error").removeClass("hidden");
									$("#vc_newsletter-form-success").addClass("hidden");
		
									$("#newsletter-form #email")
										.blur()
										.closest(".control-group")
										.removeClass("success")
										.addClass("error");
									
								}						
							}
						});	
					},		
					success: function(data){
						$(data).closest(".control-group").removeClass("error").addClass("success");
					},
					error: function() {
						$('.vc_newsletter-form-success').html('Sorry, an error occurred.').css('color', 'red');
					}		 		
		}); 
		
		/* Twitter Feeds. 
		   Used: Footer  */
		$("#twitter-feeds").tweet({
			modpath: '/php/twitter/',
			username: "envato",
			join_text: "auto",
			count: 8,
			auto_join_text_default: "we said,", 
			auto_join_text_ed: "we",
			auto_join_text_ing: "we were",
			auto_join_text_reply: "we replied to",
			auto_join_text_url: "we were checking out",
			loading_text: "loading tweets..."
		});
		$('.tweet_list').addClass('vc_li vc_carousel');		
				
		/* Primary Menu Responsive Button
		   used: header
		*/	
/*		var megamenu_link=$("header .vc_menu div.vc_primary-menu > ul .vc_mega-menu").prev();
		var href=megamenu_link.attr('href');
		megamenu_link.attr('data-link',href);*/
		
		$("header .vc_menu div.vc_primary-menu > ul .vc_mega-menu").prev()
			.append('<i class="icon-chevron-sign-down mega-menu-drop-down" style="display:none; "></i>')
			.attr('href','javascript:void(0);')
			.click(function(){
				if( $(window).width() < 979){
					$(this).next().toggle();	
				}
		});
		
		$(".btn-navbar").click(function () {
			$("header .vc_menu div.vc_primary-menu > ul").css('height','480px');
			$(".mega-menu-drop-down").toggle();
			$('div.vc_primary-menu > ul').tinyscrollbar({
				axis: 'y',
				size: '300',
				sizethumb: 'auto'
			});		
		});		

/*		$('.btn-navbar').toggle(function () {
			$(".vc_menu").addClass("active");
		}, function () {
			$(".vc_menu").removeClass("active");
		});*/	
		/* End of Code */		
		
});

/* Window Load State. 
   Used: Global 
   Info: Basically we gather up all carousels in this state
   */	
$(window).load(function()
	{		
		/* Latest Portfolio Carousel Widget.  
		   Used: index.html, index-portfolio.html, index-revolution.html */
		$(".vc_latest-portfolio .vc_carousel").carouFredSel({
			responsive: true,
			prev:{
				button : function(){
					return $(this).parent().parent().parent().children('.vc_carousel-control').children('a:first-child')
				}
			},
			next:{
				button : function(){
					return $(this).parent().parent().parent().children('.vc_carousel-control').children('a:last-child')
				}
			},
			width: '100%',
			circular: false,
			infinite: true,
			auto:{
				play : true,
				pauseDuration: 0,
				duration: 2000
			},
			scroll:{
				items: 1,
				duration: 400,
				wipe: true
			},
			items:{
				visible:{
					min: 1,
					max: 3
				},
				width: 480,
				height: 'auto'
			}
		});

		/* Client Carousel Widget.  
		   Used: index.html, index-portfolio.html, index-revolution.html, about.html */
		$(".vc_client .vc_carousel").carouFredSel({
			responsive: true,
			prev:{
				button : function(){
					return $(this).parent().parent().parent().children('.met_carousel_control').children('a:first-child')
				}
			},
			next:{
				button : function(){
					return $(this).parent().parent().parent().children('.met_carousel_control').children('a:last-child')
				}
			},
			width: 'auto',
			circular: false,
			infinite: true,
			auto:{
				play : true,
				pauseDuration: 0,
				duration: 1000
			},
			items:{
				visible:{
					min: 1,
					max: 6
				},
				height: 152
			},
			pagination  : "#vc_client-pager"
		});

		/* Testimonial Carousel Widget.  
		   Used: about.html */
		$(".vc_testimonial .vc_carousel").carouFredSel({
			responsive: true,
			width: 'auto',
			circular: false,
			infinite: true,
			auto:{
				play : true,
				pauseDuration: 0,
				duration: 1000
			},
			items:{
				visible: 1,
				height: 'auto'
			},
			pagination  : "#vc_testimonial-pager"
		});
		
		/* Sidebar Portfolio Carousel Widget.  
		   Used: Every page with sidebar */		
		$(".vc_portfolio-widget .vc_carousel").carouFredSel({
			responsive: true,
			prev:{
				button : function()
				{
					return $(this).parent().parent().parent().children('.vc_carousel-control').children('a:first-child')
				}
			},
			next:{
				button : function()
				{
					return $(this).parent().parent().parent().children('.vc_carousel-control').children('a:last-child')
				}
			},
			width: '100%',
			circular: false,
			infinite: true,
			auto:{
				play : true,
				pauseDuration: 0,
				duration: 2000
			},
			items:{
				visible:{
					min: 1,
					max: 2
				},
				width:480,
				height: 'auto'
			}
		});
		
		/* Single Portfolio Carousel Widget.  
		   Used: portfolio-single-project-full-width.html, portfolio-single-project.html */				
		$(".vc_single-portfolio .vc_carousel").carouFredSel({
			responsive: true,
			prev:{
				button : function(){
					return $(this).parent().parent().parent().children('.vc_carousel-control').children('a:first-child')
				}
			},
			next:{
				button : function(){
					return $(this).parent().parent().parent().children('.vc_carousel-control').children('a:last-child')
				}
			},
			width: '100%',
			circular: false,
			infinite: true,
			auto:{
				play : true,
				pauseDuration: 0,
				duration: 2000
			},
			items:{
				visible:{
					max: 1
				},
				width:570,
				height: 'auto'
			}
		});	
		
		/* Related Project Carousel Widget.  
		   Used: portfolio-single-project-full-width.html, portfolio-single-project.html, blog-single.html */
		$(".vc_related-project .vc_carousel").carouFredSel({
			responsive: true,
			prev:{
				button : function()
				{
					return $(this).parent().parent().parent().children('.vc_carousel-control').children('a:first-child')
				}
			},
			next:{
				button : function(){
					return $(this).parent().parent().parent().children('.vc_carousel-control').children('a:last-child')
				}
			},
			width: '100%',
			circular: false,
			infinite: true,
			auto:{
				play : true,
				pauseDuration: 0,
				duration: 2000
			},
			scroll:{
				items: 4,
				duration: 400,
				wipe: true
			},
			items:{
				visible:{
					min: 1,
					max: 4
				},
				width: 277,
				height: 'auto'
			}
		});		

		/* Twitter Carousel Widget. 
		   Used: footer 3rd Column  */			   
		update_twitter();
		$(window).resize(function(){
			update_twitter(); /*To Update Twitter Width on Carousel */
		});		   	
		function update_twitter(){
			$(".vc_twitter .vc_carousel").carouFredSel({
				direction: "down",
				height: 'variable',
				width: 'auto',
				items: 3,
				auto: 4000,
				circular: false,
				infinite: true,
				prev:{
					button : function(){
						return $(this).parent().parent().parent().parent().children('.vc_carousel-control').children('a:first-child')
					}
				},
				next:{
					button : function(){
						return $(this).parent().parent().parent().parent().children('.vc_carousel-control').children('a:last-child')
					}
				}
			});
		}	

		/* Portfolio List. 
		   Used: portfolio-2-columns.html, portfolio-3-columns.html, portfolio-4-columns.html, portfolio-left-sidebar.html, portfolio-right-sidebar.html  */
		var $container = $('#portfolio');		
		$container.isotope();	
		$('#portfolio-filter a').click(function(){			
			$('#portfolio-filter li').removeClass('active');
			$(this).parent('li').addClass('active');
			var selector = $(this).attr('data-filter');							
			$container.isotope({ filter: selector });
			return false;			
		});	
		$(window).resize(function() {								
			$container.isotope('reLayout');
		});		
								
});


/* Metro Slider.  
   Used: index.html, index-portolio.html */
   
/* Metro Slider Variable Setting */
var iFrequency = 5000; // slide movement period expressed in miliseconds
var movement = 2; // how many movement/transition
var pauseOnHover = 1; // 1 = Pause on Hover, 0 = Not Pause on Hover   


jQuery(document).ready(function($){
	metro_slider();			
	startLoop();	
});

$(window).resize(function(){
	metro_slider(); /*Do Metro Slider */
	if ($(window).width()>962){ /* Remove Collapse On width > 962 */	
		$(".mega-menu-drop-down").css('display','none');		
		$('.vc_primary-menu').removeClass('in collapse').removeAttr('style');
		$('.vc_primary-menu .vc_mega-menu').removeAttr('style');

	}
});

function metro_slider(){
	var metroSlider = 0;
	$('.vc_metro-slider ul li').each(function(){
			var slide_width= $(this).width();
			if ($(this).hasClass('odd')){
				metroSlider = metroSlider + slide_width + 5;

			} else if ($(this).hasClass('even')){
				metroSlider = metroSlider + slide_width +5 ;
			}
	});	
	metroSlider+=5;
	$('.vc_metro-slider').css('width', metroSlider + 'px');
 
	var padding=70;
	var scrollwidth=$('.container').width()-padding;
			
	$('.vc_metro-wrapper').tinyscrollbar({
		axis: 'x',
		size: scrollwidth,
		sizethumb: 'auto',
		invertscroll: true
	});
}


var iDo=0;
var myInterval;
var target =  $(".vc_metro-wrapper");

function startLoop(){
	myInterval = setInterval( runSlide, iFrequency );  // run
}
function stopLoop(){
	clearInterval(myInterval);
}

target.hover(function() { 
  		if (pauseOnHover) { stopLoop(); }
	},function() {  	
		if (pauseOnHover) { startLoop(); }
});
			   
function runSlide(){
	var pos= $('.vc_metro-wrapper .thumb').position();
	var barpos = pos.left;

	var pos= $('.vc_metro-wrapper .overview').position();
	var slidepos = pos.left;

	//increment calculation
	var barinc= ($('.vc_metro-wrapper .track').width() - $('.vc_metro-wrapper .thumb').width()) / movement;
	
	var widthconst = $(window).width();
	if ($('body').hasClass('boxed')) { widthconst = $('.container').width()+50; }
	
	var slideinc= (widthconst - $('.vc_metro-wrapper .vc_metro-slider').width()) / movement;

	// make 0 again when reach max
	iDo = (iDo+ 1) % (movement+1);

	//animate the metroslider
	$('.vc_metro-wrapper .thumb').animate({
			left: iDo * barinc
		}, 1000, function(){
	});
	$('.vc_metro-wrapper .overview').animate({
			left: iDo * slideinc -iDo*2.5
		}, 1000, function(){
	});
}
