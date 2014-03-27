<title>Сообщение отправлено</title>
<?php

if (isset($_POST['fio'])) {$fio = $_POST['fio']; if ($fio == '') {unset($fio);}}
if (isset($_POST['email'])) {$email = $_POST['email']; if ($email == '') {unset($email);}}
if (isset($_POST['problem'])) {$problem = $_POST['problem']; if ($problem == '') {unset($problem);}}
if (isset($_POST['money'])) {$money = $_POST['money']; if ($money == '') {unset($money);}}
if (isset($_POST['pr'])){$pr = $_POST['pr']; if ($pr == '') {unset($pr);}}
if (isset($_POST['captcha'])){$captcha = $_POST['captcha'];}

 


if (isset($fio) && isset($email) && isset($money) && isset($pr))
{


$fio = htmlspecialchars(trim($fio));
$email = htmlspecialchars(trim($email));
$money = htmlspecialchars(trim($money));
$problem = htmlspecialchars(trim($problem));


if(!preg_match("/[0-9a-z_]+@[0-9a-z_^\.]+\.[a-z]{2,3}/i", $email))
{
echo "<p>Неправильный формат e-mail адреса!</p>";
}


  if ($captcha == $pr)
  {

$address = "support-isef@yandex.ru";
$sub = "Сообщение";
$mes = "Автор: $fio \n E-mail: $email \n Вопрос/проблема: $problem \n Текст сообщения: $money";

$verify = mail ($address,$sub,$mes,"Content-type:text/plain; charset = UTF-8\r\nFrom:$email");
      if ($verify == 'true')
    
     {
       echo "<body bgcolor='#585858'>
<div style='margin-top: 30px'><table border='1' width='450' align='center' cellpadding='20' cellspacing='6' bgcolor='white'>
<tr>
<td>
<div style='margin'><div align='center'>Ваше сообщение успешно отправлено! <p>Вернуться на <a href='http://isef.me'><font size='+1'>www.isef.me</font></a></div>

<p><div align='center'>В ближайшее время Ваше сообщение будет рассмотрено!</div>
</tr>
</td>
</table></div>";
      }
      else 
	  {
	  echo "Сообщение не отправлено!";
	  }
  }
  else
  {
  echo "Вы не правильно ввели сумму чисел с картинки";
  }
 

}
else
{
echo "Вы заполнили не все поля!";
}
?>