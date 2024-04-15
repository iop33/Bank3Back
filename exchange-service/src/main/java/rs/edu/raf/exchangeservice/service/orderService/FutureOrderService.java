package rs.edu.raf.exchangeservice.service.orderService;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import rs.edu.raf.exchangeservice.domain.dto.BuyFutureDto;
import rs.edu.raf.exchangeservice.domain.model.listing.Future;
import rs.edu.raf.exchangeservice.domain.model.order.FutureOrder;
import rs.edu.raf.exchangeservice.repository.listingRepository.FutureRepository;
import rs.edu.raf.exchangeservice.repository.orderRepository.FutureOrderRepository;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor
public class FutureOrderService {
    private final FutureOrderRepository futureOrderRepository;
    private final FutureRepository futureRepository;

    public CopyOnWriteArrayList<FutureOrder> ordersToBuy = new CopyOnWriteArrayList<>();
    public CopyOnWriteArrayList<FutureOrder> ordersToApprove = new CopyOnWriteArrayList<>();

    public List<FutureOrder> findAll(){
        return futureOrderRepository.findAll();
    }

    public List<FutureOrder> findAllByEmployee(Long id){
        return futureOrderRepository.findByEmployeeId(id);
    }

    public List<FutureOrder> getAllOrdersToApprove(){
        return this.ordersToApprove;
    }

    public FutureOrder approveFutureOrder(Long id){
        FutureOrder futureOrder = futureOrderRepository.findByFutureOrderId(id);

        return this.futureOrderRepository.save(futureOrder);
    }

    public String buyFuture(BuyFutureDto buyFutureDto){
        FutureOrder futureOrder = new FutureOrder();
        futureOrder.setEmployeeId(buyFutureDto.getEmployeeId());
        futureOrder.setFutureId(buyFutureDto.getFutureId());
        futureOrder.setPrice(buyFutureDto.getPrice());

        this.ordersToBuy.add(this.futureOrderRepository.save(futureOrder));

        return "UBACENO U ORDER";
    }


    @Scheduled(fixedRate = 45000)
    public void executeTask() {

        if (ordersToBuy.isEmpty()) {
            System.out.println("Executing task every 15 seconds, but list to buy is empty :-(");
        } else {
            Random rand = new Random();
            int futureNumber = rand.nextInt(ordersToBuy.size());
            FutureOrder futureOrder = ordersToBuy.get(futureNumber);   //StockOrder koji obradjujemo

            Future future = null;
            Optional<Future> optionalFuture = this.futureRepository.findById(futureOrder.getFutureId());
            if(optionalFuture.isPresent())
                future = optionalFuture.get();  //uzimao future iz baze koji kupujemo
            else {
                System.out.println("No futures available with id "+futureOrder.getFutureId()+", restarting in 15 seconds...");
                return;
            }

            //TODO naci broj racuna banke i berze preko valute

            this.futureOrderRepository.save(futureOrder); //cuvamo promenjene vrednosti u bazi
        }
    }

}
