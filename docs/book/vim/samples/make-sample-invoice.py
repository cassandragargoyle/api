# /// script
# dependencies = ["weasyprint>=62", "segno>=1.6"]
# ///
#
# This file is part of CassandraGargoyle Community Project
# Licensed under the MIT License - see LICENSE file for details
#
"""Make the synthetic sample invoices of the VIM book (all names and numbers are invented)

Usage:
    uv run docs/book/vim/samples/make-sample-invoice.py      # writes both PDFs next to this file
"""
import base64
import io
from pathlib import Path

import segno
from weasyprint import HTML

HERE = Path(__file__).resolve().parent

TEXTS = {
    "faktura-2026-0042.pdf": {
        "title": "Faktura 2026-0042",
        "supplier": "Dodavatel", "supplier_lines": ["Sklo a vrstvy s.r.o.", "Polní 7", "110 00 Praha 1"],
        "customer": "Odběratel", "customer_lines": ["Jana Příkladová", "Lipová 12", "602 00 Brno"],
        "date_label": "Datum vystavení", "date": "15. 10. 2026",
        "head": ["Položka", "Množství", "Cena", "Celkem"],
        "rows": [["Skleněná deska A", "2", "300,00", "600,00"], ["Skleněná deska B", "1", "650,00", "650,00"]],
        "total_label": "Celkem k úhradě", "total": "1 250,00 CZK",
        "message": "Faktura 2026-0042",
    },
    "invoice-2026-0042.pdf": {
        "title": "Invoice 2026-0042",
        "supplier": "Supplier", "supplier_lines": ["Glass & Layers Ltd.", "7 Field Lane", "110 00 Prague 1"],
        "customer": "Customer", "customer_lines": ["Jane Sample", "12 Linden Street", "602 00 Brno"],
        "date_label": "Issue date", "date": "15 October 2026",
        "head": ["Item", "Quantity", "Price", "Total"],
        "rows": [["Glass panel A", "2", "300.00", "600.00"], ["Glass panel B", "1", "650.00", "650.00"]],
        "total_label": "Total due", "total": "1,250.00 CZK",
        "message": "Invoice 2026-0042",
    },
}


def make(name: str, t: dict) -> None:
    qr = segno.make(
        f"SPD*1.0*ACC:CZ6508000000192000145399*AM:1250.00*CC:CZK*X-VS:20260042*MSG:{t['message']}",
        error="m",
    )
    buf = io.BytesIO()
    qr.save(buf, kind="png", scale=6)
    b64 = base64.b64encode(buf.getvalue()).decode()
    rows = "".join("<tr>" + "".join(f"<td>{c}</td>" for c in r) + "</tr>" for r in t["rows"])
    head = "".join(f"<th>{c}</th>" for c in t["head"])
    html = f"""<html><body style="font-family:DejaVu Sans;font-size:11pt;margin:20mm">
<h1 style="font-size:20pt">{t['title']}</h1>
<table style="width:100%"><tr>
<td style="vertical-align:top;width:50%"><b>{t['supplier']}</b><br>{'<br>'.join(t['supplier_lines'])}</td>
<td style="vertical-align:top"><b>{t['customer']}</b><br>{'<br>'.join(t['customer_lines'])}</td></tr></table>
<p><b>{t['date_label']}</b><br>{t['date']}</p>
<table border="1" style="border-collapse:collapse;width:100%;margin-top:8mm">
<tr>{head}</tr>{rows}</table>
<p style="margin-top:6mm"><b>{t['total_label']}</b><br>{t['total']}</p>
<img src="data:image/png;base64,{b64}" style="width:35mm">
</body></html>"""
    HTML(string=html).write_pdf(HERE / name)


for file_name, texts in TEXTS.items():
    make(file_name, texts)
