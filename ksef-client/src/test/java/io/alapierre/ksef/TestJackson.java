package io.alapierre.ksef;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.junit.Test;
import pl.akmf.ksef.sdk.client.model.invoice.InvoiceQueryFilters;

/**
 * @author Adrian Lapierre {@literal al@alapierre.io}
 * Copyrights by original author 1.12.2025
 */
public class TestJackson {

    @Test
    public void testSerialize() throws Exception {

        InvoiceQueryFilters q = new InvoiceQueryFilters();
        q.setSelfInvoicing(true);

        ObjectMapper mapper = new ObjectMapper();

        val res = mapper.writeValueAsString(q);

        System.out.println(res);


    }

}
