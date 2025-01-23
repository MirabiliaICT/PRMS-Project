package ng.org.mirabilia.pms.services.implementations;


import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import ng.org.mirabilia.pms.domain.entities.GltfModel;
import ng.org.mirabilia.pms.domain.entities.PaymentReceipt;
import ng.org.mirabilia.pms.repositories.ReceiptImageRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ReceiptImageService {

    ReceiptImageRepository receiptImageRepository;

    @Transactional
    public void deleteExistingModel(PaymentReceipt paymentReceipt) {
        receiptImageRepository.deleteById(paymentReceipt.getId());
    }

    public PaymentReceipt save(PaymentReceipt paymentReceipt) {
        return receiptImageRepository.save(paymentReceipt);
    }

}
