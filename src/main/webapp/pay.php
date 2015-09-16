<!DOCTYPE html>

<?php
if(empty($_GET["amount"]) || empty($_GET["email"])) {
	echo "Wrong payment";
	return;
}
?>

<html>
<head>
    <meta charset="UTF-8" />
    <title>isef</title>
<!-- Начало кода для вставки на страницу в тег <head> -->
    <meta name="Pokupo.activationKey" content="d64ce8d6017f0d3ab6d528cf5dfd616ae05c32b4" />
    <meta name="Pokupo.version" content="0.0.1.7" />
    <link href="https://pokupo.ru/styles/payment.css" type="text/css" rel="stylesheet">
    <script src="https://server.pokupo.ru/prod/server/js/jquery-1.11.2.js" type="text/javascript"></script>
    <script src="https://server.pokupo.ru/prod/server/js/widgets/JSCore-1.2.js" type="text/javascript"></script>
    <script src="https://server.pokupo.ru/prod/server/js/widgets/StandalonePaymentWidget-1.0.js" type="text/javascript"></script>
    <script src="https://server.pokupo.ru/prod/server/js/promo/AnimateStandalonePayment-1.0.js" type="text/javascript"></script>
    <script src="https://server.pokupo.ru/prod/server/js/widgets/StatusPaymentWidget-1.0.js" type="text/javascript"></script>
    <script src="https://server.pokupo.ru/prod/server/js/promo/AnimateStatusPayment-1.0.js" type="text/javascript"></script>
    <script src="https://server.pokupo.ru/prod/server/js/promo/jquery.fs.selecter.js" type="text/javascript"></script>
    <script src="https://server.pokupo.ru/prod/server/js/promo/jquery.mCustomScrollbar.concat.min.js" type="text/javascript"></script>
    <script type="text/javascript">
        var pkpWHostTmpl = 'https://pokupo.ru/themes/default/tmpl';
        WParameters = {
            core: {
                shopId: 757, //Id магазина, который владеет товаром
                imgLoader: '/images/loader_32px.gif',
                host: 'server.pokupo.ru/prod/server/',
                pathToData : 'prod/server/services/DataProxy.php?query=',
                pathToPostData : 'services/DataPostProxy.php',
                hostApi : 'api.pokupo.ru/'
            }
        };
        WParameters['standalonePayment'] = {
            container: {
                button: {widget: 'content'},
                content: {widget: 'content'}
            },
            tmpl: {path:  pkpWHostTmpl + '/standalonePaymentTmpl.html'}
        };
        WParameters['statusPayment'] = {
            container: {widget: 'content'},
            tmpl: {path: pkpWHostTmpl + '/statusPaymentTmpl.html'}
        };
    </script>
<!-- Конец кода для вставки на страницу в тег <head> -->
</head>
<body>

<!-- Начало кода примера динамической ссылки для оплаты произвольной суммы -->
<!--a style="background-color: #ffa800;border-radius: 40px;color: #000000;display: block;font-family: Arial, sans-serif;font-size: 13px;font-weight: bold;line-height: 46px;text-align: center;text-decoration: none;margin:0 auto;width: 160px;margin-top: 10px;text-transform: uppercase;" href="#" onclick="Routing.SetHash('standalone_payment', '', {description: 'В пользу магазина isef', idShopPartner: 757, mailUser: '' }); return false;">Оплатить</a-->
<!-- Конец кода примера динамической ссылки для оплаты произвольной суммы -->

<!-- Начало кода примера динамической ссылки для оплаты определенной суммы -->
<a style="background-color: #ffa800;border-radius: 40px;color: #000000;display: block;font-family: Arial, sans-serif;font-size: 13px;font-weight: bold;line-height: 46px;text-align: center;text-decoration: none;margin:0 auto;width: 180px;margin-top: 10px;text-transform: uppercase;" href="#" onclick="Routing.SetHash('standalone_payment', '', {description: 'В пользу магазина isef', amount: <?php echo $_GET["amount"]; ?>, idShopPartner: 757, mailUser: '<?php echo $_GET["email"]; ?>' }); return false;">Оплатить <?php echo $_GET["amount"]; ?> руб</a>
<!-- Конец кода примера динамической ссылки для оплаты определенной суммы -->

<!-- Начало кода для контейнера виджета -->
<div id="content"></div>
<!-- Конец кода для контейнера виджета -->
</body>
</html>