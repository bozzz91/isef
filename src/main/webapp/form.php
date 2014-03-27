<div align="center">
<table border="1" width="365" align="center" cellpadding="20" cellspacing="6">
<tr>
<td>
<form action="http://isef.me/options.php" method="post">

<p>Ваше имя/ник:
 <input type="text" name="fio" width="20">
 <p>Ваш e-mail:
 <input type="text" name="email" width="20">
 <p>Вопрос/проблема:
 <input type="text" name="problem" width="20">
 <p>Текст сообщения:
<textarea name="money" cols="40" rows="5"></textarea>
<p>Введите числа с картинки: 
		<?php 
		$i=1;
		do
		{
		$num[$i] = mt_rand(0,9);
		echo "<img src='img/".$num[$i].".gif' border='0' align='bottom' vspace='5px'>";
		$i++;
		}
		while ($i<5);
		$captcha = $num[1].$num[2].$num[3].$num[4];
		?>
<input name="captcha" type="hidden" value="<?php echo $captcha ;?>">
<input name="pr" style=" margin-bottom:11px" type="text" size="6" maxlength="4"></p>
<p><input type="submit" style="height:50px;" value="Отправить сообщение"></p>
</form>
</tr>
</td>
</table>
</div>