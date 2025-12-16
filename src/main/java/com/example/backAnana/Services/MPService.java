package com.example.backAnana.Services;

import com.example.backAnana.Entities.Enums.FormaPago;
import com.example.backAnana.Entities.PreferenceMP;
import com.example.backAnana.Entities.Venta;
import com.example.backAnana.Repositories.VentaRepository;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MPService {

    private final String tokenMP = System.getenv("MP_TOKEN");

    @Autowired
    private VentaRepository ventaRepository;

    public PreferenceMP createPreference(Venta venta) throws MPApiException {
        try {
            MercadoPagoConfig.setAccessToken(tokenMP);


            PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                    .id(String.valueOf(venta.getId()))
                    .title("Venta: "+venta.getId())
                    .description("Pedido realizado desde el carrito de compras")
                    .quantity(1)
                    .currencyId("ARS")
                    .unitPrice(new BigDecimal(venta.getTotal()))
                    .build();
            List<PreferenceItemRequest> items = new ArrayList<>();
            items.add(itemRequest);
            PreferenceBackUrlsRequest backURL = PreferenceBackUrlsRequest.builder()
                    .success("http://localhost:5173/mpsuccess")
                    .pending("http://localhost:5173/mppending")
                    .failure("http://localhost:5173/mpfailure")
                    .build();

            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(items)
                    .backUrls(backURL)
                    .externalReference(String.valueOf(venta.getId()))
                    .build();
            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);

            PreferenceMP mpPreference = new PreferenceMP();
            mpPreference.setStatusCode(preference.getResponse().getStatusCode());
            mpPreference.setId(preference.getId());
            mpPreference.setInit_point(preference.getInitPoint());
            return mpPreference;

        } catch (com.mercadopago.exceptions.MPApiException e) {
            System.out.println("❌ MPApiException:");
            System.out.println("Status Code: " + e.getApiResponse().getStatusCode());
            System.out.println("Response: " + e.getApiResponse().getContent());
            throw e; // O si querés retornar un error JSON, lo manejás en el controller
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al generar preferencia de Mercado Pago", e);
        }
    }

    public String procesarWebhook(String topic, String idPago) {
        try {
            if (!"payment".equals(topic)) {
                return "Evento no manejado: " + topic;
            }

            MercadoPagoConfig.setAccessToken(tokenMP);

            PaymentClient paymentClient = new PaymentClient();
            Payment payment = paymentClient.get(Long.parseLong(idPago));

            if ("approved".equals(payment.getStatus())) {
                String ventaIdStr = payment.getExternalReference();
                Long ventaId = Long.parseLong(ventaIdStr);

                Optional<Venta> optionalVenta = ventaRepository.findById(ventaId);
                if (optionalVenta.isPresent()) {
                    Venta venta = optionalVenta.get();
                    venta.setFormaPago(FormaPago.MERCADOPAGO);
                    ventaRepository.save(venta);
                    return "Venta actualizada con éxito.";
                } else {
                    return "Venta no encontrada con ID: " + ventaIdStr;
                }
            } else {
                return "Pago recibido pero no aprobado: " + payment.getStatus();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Error procesando webhook: " + e.getMessage();
        }
    }

}
