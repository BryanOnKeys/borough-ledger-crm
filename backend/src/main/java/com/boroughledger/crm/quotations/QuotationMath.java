package com.boroughledger.crm.quotations;
import java.math.*;
public final class QuotationMath {
 private QuotationMath(){}
 public record Breakdown(BigDecimal gross,BigDecimal discount,BigDecimal tax,BigDecimal total){}
 public static Breakdown calculate(BigDecimal unitPrice,BigDecimal qty,BigDecimal discountPercent,BigDecimal taxPercent){
  if(unitPrice==null||unitPrice.signum()<0)throw new IllegalArgumentException("Precio inválido");if(qty==null||qty.signum()<=0)throw new IllegalArgumentException("Cantidad positiva requerida");
  if(discountPercent==null||discountPercent.signum()<0||discountPercent.compareTo(new BigDecimal("100"))>0)throw new IllegalArgumentException("Descuento entre 0 y 100");
  if(taxPercent==null||taxPercent.signum()<0||taxPercent.compareTo(new BigDecimal("100"))>0)throw new IllegalArgumentException("Impuesto entre 0 y 100");
  BigDecimal gross=unitPrice.multiply(qty).setScale(2,RoundingMode.HALF_UP);
  BigDecimal discount=gross.multiply(discountPercent).divide(new BigDecimal("100"),2,RoundingMode.HALF_UP);
  BigDecimal taxable=gross.subtract(discount);
  BigDecimal tax=taxable.multiply(taxPercent).divide(new BigDecimal("100"),2,RoundingMode.HALF_UP);
  return new Breakdown(gross,discount,tax,taxable.add(tax));
 }
}
