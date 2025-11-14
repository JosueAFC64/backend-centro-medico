package cm.apicitamedica.client.slot;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "ApiHorario", fallbackFactory = DetalleHorarioFallBackFactory.class)
public interface DetalleHorarioFeignClient {

    @GetMapping("/horarios/client/{idHorario}/slots/{idDetalle}")
    SlotClientResponse obtenerSlot(@PathVariable("idHorario") Long idHorario,
                                   @PathVariable("idDetalle") Long idDetalle);

    @PutMapping("/horarios/{idHorario}/slots/{idDetalle}/ocupar")
    void ocuparSlot(@PathVariable("idHorario") Long idHorario,
                    @PathVariable("idDetalle") Long idDetalle,
                    @RequestParam("idCita") Long idCita);

    @PutMapping("/horarios/{idHorario}/slots/{idDetalle}/liberar")
    void liberarSlot(@PathVariable("idHorario") Long idHorario,
                     @PathVariable("idDetalle") Long idDetalle);

}
