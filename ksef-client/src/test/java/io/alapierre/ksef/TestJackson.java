package io.alapierre.ksef;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import pl.akmf.ksef.sdk.client.model.invoice.InvoiceQueryFilters;

import static org.junit.Assert.*;

/**
 * @author Adrian Lapierre {@literal al@alapierre.io}
 * Copyrights by original author 1.12.2025
 */
public class TestJackson {

    @Test
    public void selfInvoicingUsesApiPropertyName() throws Exception {

        InvoiceQueryFilters q = new InvoiceQueryFilters();
        q.setSelfInvoicing(true);

        ObjectMapper mapper = new ObjectMapper();

        var json = mapper.readTree(mapper.writeValueAsBytes(q));
        assertTrue(json.has("isSelfInvoicing"));
        assertTrue(json.get("isSelfInvoicing").booleanValue());
        assertFalse(json.has("selfInvoicing"));
        assertEquals(Boolean.TRUE, mapper.readValue("{\"isSelfInvoicing\":true}",
                InvoiceQueryFilters.class).getSelfInvoicing());
    }

}
