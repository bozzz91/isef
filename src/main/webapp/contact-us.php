<!DOCTYPE html>
<html lang="ru">
    <head>
        <meta charset="utf-8" />
        <title>ISef</title>
        <link rel="stylesheet" type="text/css" href="/style.css">
        <link rel="shortcut icon" type="image/x-icon" href="/imgs/favicon.ico">
        <link rel="icon" type="image/x-icon" href="/imgs/favicon.ico">
        <meta name="keywords" content="" />
        <meta name="description" content="">
        <meta name="author" content="Venmond">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link href="css/fonts.css"  rel='stylesheet' type='text/css'>
        <!--[if lte IE 9]><link type='text/css' rel="stylesheet" href="css/fonts/ie-google-webfont.css"><![endif]-->
        <link href='css/bootstrap.min.css' rel='stylesheet' type='text/css'>
        <link href="css/font-awesome.min.css" rel="stylesheet" type='text/css'>
        <!--[if IE 7]><link type='text/css' rel="stylesheet" href="css/font-awesome-ie7.min.css"><![endif]-->
        <link href="plugins/prettyPhoto-plugin/css/prettyPhoto.css" rel="stylesheet" type='text/css'>
        <link href="plugins/isotope-plugin/css/isotope.css" rel="stylesheet" type='text/css'>
        <link rel="stylesheet" type="text/css" href="plugins/rs-plugin/css/settings.css" media="screen">
        <link href='css/theme.css' rel='stylesheet' type='text/css'>
        <link href='css/bootstrap-responsive.min.css' rel='stylesheet' type='text/css'>
        <link href='css/theme-responsive.css' rel='stylesheet' type='text/css'>
        <link id="link-design" href='css/mode/mode-slash.css' rel='stylesheet' type='text/css'>
        <link id="link-color" href='css/color/color-yellow.css' rel='stylesheet' type='text/css'>
        <link href='custom/custom.css' rel='stylesheet' type='text/css'>
    </head>

    <body class="clearfix" data-smooth-scrolling="1">
        <div class="vc_body">
            <header>
                <div class="container">
                    <div class="row-fluid">
                        <div class="span12">
                            <nav class="vc_menu pull-left"> <a href="index.html" class="logo pull-left"> <img alt="logo" src="img/logo.png"> </a>
                                <div class="navbar navbar-inverse">
                                    <button type="button" class="btn btn-navbar" data-toggle="collapse" data-target=".vc_primary-menu"> <span class="icon-bar"> </span> <span class="icon-bar"> </span> <span class="icon-bar"> </span> </button>
                                </div>
                                <div class="vc_primary-menu pull-left">
                                    <ul>
                                        <li> <a href="index.html"> Главная<i class="icon-caret-down"> </i> </a>
                                        </li>
                                        <li> <a href="http://isef.me/login/"> Вход/Регистрация <i class="icon-caret-down"> </i> </a> 
                                        </li> 
                                        <li> <a href="faq.html"> FAQ <i class="icon-caret-down"> </i> </a> </li>
										<li> <a href="contact-us.php"> Обратная связь <i class="icon-caret-down"> </i> </a> </li>
                                    </ul>
                                </div>
                            </nav>
                            <div class="clearfix"> </div>
                            <div class="vc_secondary-menu row-fluid">
                                <div class="vc_contact-top-wrapper span9 row-fluid">
                                    <div class="vc_contact-top pull-right">
                                        <div class="pull-left">
                                            <h5> <span> <i class="icon-envelope"> </i> support-isef@yandex.ru </span> </h5>
                                        </div>
                                    </div>
                                </div>
                                <div class="vc_social-share-wrapper span3">
                                    <div class="vc_social-share pull-right"> <a title="VK" class="twitter" href="#"> Vkontakte </a> <!--<a title="Facebook" class="facebook" href="#"> Facebook </a>--> <a title="email" class="email" href="contact-us.php"> Email </a> </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="vc_sub-menu-bg"> </div>
                </div>
                <div class="vc_menu-bg"> </div>
            </header>  

          
            <!-- vc_banner -->
        </div>

        <a class="back-top" href="#" id="back-top"> <i class="icon-chevron-up icon-white"> </i> </a> 

        <!-- Javascript =============================================== --> 
        <!-- Placed at the end of the document so the pages load faster --> 
        <script src="js/modernizr.js"></script> 
        <script src="js/jquery.js"></script> 
        <script src="js/bootstrap.min.js"></script> 
        <script src="js/retina.js"></script> 
        <script src="js/tinyscrollbar.js"></script> 
        <script src="js/caroufredsel.js"></script> 
        <script src="js/plugins.js"></script>
        <script src="plugins/prettyPhoto-plugin/js/jquery.prettyPhoto.js"></script> 
        <script src="plugins/isotope-plugin/js/jquery.isotope.min.js"></script> 
        <script src="php/twitter/jquery.tweet.js"></script>
        <script src="js/theme.js"></script>
        <script src="custom/custom.js"></script>


        <!-- InstanceBeginEditable name="script" -->
        <!-- Specific Page Scripts Put Here -->
        <script type="text/javascript" src="plugins/rs-plugin/js/jquery.themepunch.plugins.min.js"></script>
        <script type="text/javascript" src="plugins/rs-plugin/js/jquery.themepunch.revolution.min.js"></script>
        <script>
            var tpj = jQuery;

            tpj(document).ready(function() {
                setTimeout(function() {
                    if (tpj.fn.cssOriginal != undefined)
                        tpj.fn.css = tpj.fn.cssOriginal;

                    tpj('.banner').revolution(
                            {
                                delay: 9000,
                                startheight: 500,
                                startwidth: 1170,
                                hideThumbs: 200,
                                thumbWidth: 100, // Thumb With and Height and Amount (only if navigation Tyope set to thumb !)
                                thumbHeight: 50,
                                thumbAmount: 5,
                                navigationType: "bullet", // bullet, thumb, none
                                navigationArrows: "solo", // nexttobullets, solo (old name verticalcentered), none

                                navigationStyle: "round", // round,square,navbar,round-old,square-old,navbar-old, or any from the list in the docu (choose between 50+ different item), custom

                                navigationHAlign: "center", // Vertical Align top,center,bottom
                                navigationVAlign: "bottom", // Horizontal Align left,center,right
                                navigationHOffset: 0,
                                navigationVOffset: 20,
                                soloArrowLeftHalign: "left",
                                soloArrowLeftValign: "center",
                                soloArrowLeftHOffset: 20,
                                soloArrowLeftVOffset: 0,
                                soloArrowRightHalign: "right",
                                soloArrowRightValign: "center",
                                soloArrowRightHOffset: 20,
                                soloArrowRightVOffset: 0,
                                touchenabled: "on", // Enable Swipe Function : on/off
                                onHoverStop: "on", // Stop Banner Timet at Hover on Slide on/off

                                stopAtSlide: -1, // Stop Timer if Slide "x" has been Reached. If stopAfterLoops set to 0, then it stops already in the first Loop at slide X which defined. -1 means do not stop at any slide. stopAfterLoops has no sinn in this case.
                                stopAfterLoops: -1, // Stop Timer if All slides has been played "x" times. IT will stop at THe slide which is defined via stopAtSlide:x, if set to -1 slide never stop automatic

                                shadow: 1, //0 = no Shadow, 1,2,3 = 3 Different Art of Shadows  (No Shadow in Fullwidth Version !)
                                fullWidth: "off"			// Turns On or Off the Fullwidth Image Centering in FullWidth Modus
                            });
                }, 200);
            });
        </script>

        <!-- InstanceEndEditable -->
        <!-- Google Analytics: Change UA-XXXXX-X to be your site's ID. Go to http://www.google.com/analytics/ for more information. -->
        <script>
            var _gaq = _gaq || [];
            _gaq.push(['_setAccount', 'UA-XXXXX-X']);
            _gaq.push(['_trackPageview']);

            (function() {
                var ga = document.createElement('script');
                ga.type = 'text/javascript';
                ga.async = true;
                ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
                var s = document.getElementsByTagName('script')[0];
                s.parentNode.insertBefore(ga, s);
            })();
        </script>
        <table width="100%" cellpadding="0" cellspacing="0">
			<div align="center">
			<?php include "form.php";?>
			</div>
		</table>
