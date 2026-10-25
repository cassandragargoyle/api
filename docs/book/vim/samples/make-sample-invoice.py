# /// script
# dependencies = ["weasyprint>=62", "segno>=1.6"]
# ///
import segno, base64, io
qr = segno.make("SPD*1.0*ACC:CZ6508000000192000145399*AM:1250.00*CC:CZK*X-VS:20260042*MSG:Faktura 2026-0042", error="m")
buf = io.BytesIO(); qr.save(buf, kind="png", scale=6)
b64 = base64.b64encode(buf.getvalue()).decode()
html = f"""<html><body style="font-family:DejaVu Sans;font-size:11pt;margin:20mm">
<h1 style="font-size:20pt">Faktura 2026-0042</h1>
<table style="width:100%"><tr>
<td style="vertical-align:top;width:50%"><b>Dodavatel</b><br>Sklo a vrstvy s.r.o.<br>Polní 7<br>110 00 Praha 1</td>
<td style="vertical-align:top"><b>Odběratel</b><br>Jana Příkladová<br>Lipová 12<br>602 00 Brno</td></tr></table>
<p><b>Datum vystavení</b><br>15. 10. 2026</p>
<table border="1" style="border-collapse:collapse;width:100%;margin-top:8mm">
<tr><th>Položka</th><th>Množství</th><th>Cena</th><th>Celkem</th></tr>
<tr><td>Skleněná deska A</td><td>2</td><td>300,00</td><td>600,00</td></tr>
<tr><td>Skleněná deska B</td><td>1</td><td>650,00</td><td>650,00</td></tr></table>
<p style="margin-top:6mm"><b>Celkem k úhradě</b><br>1 250,00 CZK</p>
<img src="data:image/png;base64,{b64}" style="width:35mm">
</body></html>"""
from weasyprint import HTML
HTML(string=html).write_pdf("sample/faktura-2026-0042.pdf")
