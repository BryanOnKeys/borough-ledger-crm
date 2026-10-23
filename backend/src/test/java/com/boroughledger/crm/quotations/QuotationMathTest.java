package com.boroughledger.crm.quotations;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class QuotationMathTest {
 @Test void computesNetTaxAfterDiscount(){
  var a=QuotationMath.calculate(new BigDecimal("100.00"),new BigDecimal("2"),new BigDecimal("10"),new BigDecimal("18"));
  assertEquals(new BigDecimal("200.00"),a.gross());assertEquals(new BigDecimal("20.00"),a.discount());
  assertEquals(new BigDecimal("32.40"),a.tax());assertEquals(new BigDecimal("212.40"),a.total());
 }
 @Test void rejectsNegativeAmounts(){assertThrows(IllegalArgumentException.class,()->QuotationMath.calculate(new BigDecimal("-1"),BigDecimal.ONE,BigDecimal.ZERO,BigDecimal.ZERO));}
 @Test void rejectsInvalidPercent(){assertThrows(IllegalArgumentException.class,()->QuotationMath.calculate(BigDecimal.ONE,BigDecimal.ONE,new BigDecimal("101"),BigDecimal.ZERO));}
 @Test void neverUsesBinaryFloatingPoint(){var r=QuotationMath.calculate(new BigDecimal("0.10"),new BigDecimal("3"),BigDecimal.ZERO,BigDecimal.ZERO);assertEquals(new BigDecimal("0.30"),r.total());}
}
