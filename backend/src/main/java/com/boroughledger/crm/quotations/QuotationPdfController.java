package com.boroughledger.crm.quotations;
import com.boroughledger.crm.users.AppUser;
import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.*;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/quotations")
public class QuotationPdfController {
 private final QuotationService service;
 public QuotationPdfController(QuotationService service){this.service=service;}
 @GetMapping(value="/{id}/pdf",produces=MediaType.APPLICATION_PDF_VALUE)
 public ResponseEntity<byte[]> pdf(@AuthenticationPrincipal AppUser a,@PathVariable UUID id) throws IOException {
  QuotationService.View q=service.get(a,id);
  try(PDDocument doc=new PDDocument();ByteArrayOutputStream out=new ByteArrayOutputStream()){
   PDPage page=new PDPage();doc.addPage(page);
   PDPageContentStream content=new PDPageContentStream(doc,page);
   PDFont font=new PDType1Font(Standard14Fonts.FontName.HELVETICA);
   PDFont bold=new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
   float y=750;
   line(content,bold,18,56,y,"BOROUGH & LEDGER  |  COTIZACION");y-=35;
   line(content,font,11,56,y,"Numero: "+q.number());y-=19;
   line(content,font,11,56,y,"Estado: "+q.status()+"    Moneda: "+q.currency());y-=19;
   line(content,font,11,56,y,"Vigencia: "+(q.validUntil()==null?"No especificada":q.validUntil()));y-=35;
   line(content,bold,11,56,y,"PRODUCTO / SERVICIO                         CANT.             P. UNIT.         IMPORTE");y-=19;
   for(QuotationLine item:q.items()){
    if(y<105){content.close();page=new PDPage();doc.addPage(page);content=new PDPageContentStream(doc,page);y=750;}
    String name=item.getProductName(); if(name.length()>32)name=name.substring(0,32);
    line(content,font,10,56,y,String.format(java.util.Locale.ROOT,"%-34s %8s %14s %14s",name,item.getQuantity(),item.getUnitPrice(),item.getLineTotal()));y-=20;
   }
   if(y<130){content.close();page=new PDPage();doc.addPage(page);content=new PDPageContentStream(doc,page);y=750;}
   y-=10;line(content,font,11,56,y,"Subtotal: "+q.subtotal());y-=18;
   line(content,font,11,56,y,"Descuento: "+q.discountTotal());y-=18;
   line(content,font,11,56,y,"Impuestos: "+q.taxTotal());y-=24;
   line(content,bold,15,56,y,"TOTAL "+q.currency()+" "+q.total());
   content.close();doc.save(out);
   return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=cotizacion-"+q.number()+".pdf").body(out.toByteArray());
  }
 }
 private void line(PDPageContentStream c,PDFont font,int size,float x,float y,String raw)throws IOException{
  String safe=raw.replaceAll("[^\u0020-\u007E\u00A0-\u00FF]","?");c.beginText();c.setFont(font,size);c.newLineAtOffset(x,y);c.showText(safe);c.endText();
 }
}
