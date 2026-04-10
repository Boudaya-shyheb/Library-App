package soyosa.inventaire.Controller;

import org.springframework.web.bind.annotation.*;
import soyosa.inventaire.Model.Supply;
import soyosa.inventaire.Service.SupplyService;

import java.util.List;

@RestController
@RequestMapping("/api/inventaire/supplies")
public class SupplyController {

    private final SupplyService supplyService;

    public SupplyController(SupplyService supplyService) {
        this.supplyService = supplyService;
    }

    @PostMapping
    public Supply addSupply(@RequestBody Supply supply) {
        return supplyService.addSupply(supply);
    }

    @GetMapping("/book/{bookId}")
    public List<Supply> getSuppliesByBook(@PathVariable Long bookId) {
        return supplyService.getSuppliesByBook(bookId);
    }

}
