package com.example.mscmd.api;

import com.example.mscmd.aggregates.LoanReturn;
import com.example.mscmd.dao.LoanReturnRepository;
import com.example.mscmd.proxy.BooksProxy;
import com.example.mscmd.proxy.LateProxy;
import com.example.mscmd.proxy.ReaderProxy;
import com.example.mscmd.proxy.ReservationProxy;
import com.maktabatic.coreapi.commands.LoanCommand;
import com.maktabatic.coreapi.commands.ReturnCommand;
import com.maktabatic.coreapi.dto.OperationDTO;
import com.maktabatic.coreapi.model.Book;
import com.maktabatic.coreapi.model.BookState;
import com.maktabatic.coreapi.model.KeyLoanReturn;
import com.maktabatic.coreapi.model.Reader;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("command")
@RefreshScope
public class CommandController {
    @Value("${invalid.rr : Your card RFID is not valid}")
    String invalid_rr_msg;

    @Value("${punished : You could not loan now, You are punished until}")
    String punished_msg;

    @Value("${late : You are too late, You have to return the book first!}")
    String late_msg;

    @Value("${already.borrowed : You had already borrowed a book, you can not loan an other until you return it!}")
    String already_borrowed;

    @Value("${invalid.rb : This is not our library book}")
    String invalid_rb_msg;

    @Value("${first.experience : This is your first experience with MAKTABATIC, right? Welcome :)\n}")
    String first_experience;

    @Value("${book.first.use : You are the first one taking this book!\n}")
    String book_first_use;

    @Value("${valid.loan : You are welcome, You could take your book.\n Best Regarding :) }")
    String valid_loan;

    @Value("${loan.borrowed.book : This is a borrowed book!}")
    String loan_borrowed_book;

    @Value("${loan.own.reserved : You could take your reserved book :) }")
    String loan_own_reserved;

    @Value("${reserved.msg : Sorry, this is already reserved. You could take an other.}")
    String reserved_msg;

    @Value("${punish.days : 7}")
    int punishment_days;

    @Value("${no.book.return : You have no book to return}")
    String no_book_return;

    @Value("${valid.return : Thank you best regards :) }")
    String valid_return;

    @Value("${late.return : Please Try to Return Books in time for not being penalized, Now you can not loan books for ${punish.days} days}")
    String late_return;

    @Value("${return.not.loaned : This is not the loaned book}")
    String return_not_loaned;

    @Value("${no.borrowed : You have no borrowed book!}")
    String no_borrowed;

    @Value("${prolong.days : 2}")
    int prolong_valid_days;

    @Value("${day : 86400000}")
    long DAY_MILLIS;

    @Value("${waiting.msg : Sorry you can not extend the time there's is other student which are waiting for it}")
    String waiting_msg;

    @Value("${loan.peroid : 15}")
    int loan_period;

    @Value("#{'${dates}'.split(',')}")
    List<String> dates;

    @Value("#{'${weekend}'.split(',')}")
    List<String> weekend;

    @Autowired
    private CommandGateway commandGateway;

    @Autowired
    ReaderProxy readerProxy;
    @Autowired
    BooksProxy booksProxy;
    @Autowired
    ReservationProxy reservationProxy;

    @Autowired
    LoanReturnRepository loanReturnRepository;

    @Autowired
    LateProxy lateProxy;

    @PostMapping("/loan")
    public String loan(@RequestBody OperationDTO operationDTO){
        // starting to verify the existence of the RFIDs
        boolean rr = false;
        boolean rb = false;
        boolean dispo = false;
        boolean never_borrowed = false;
        boolean islender = false;
        boolean isPunished = false;
        boolean not_borrowed = false;
        boolean borrowed = false;
        boolean reserved =false;
        Reader reader = readerProxy.verifyRFIDReader(operationDTO.getRfidReader(),"toloan");

        rr = (reader != null);

        if(!rr) return invalid_rr_msg;

        never_borrowed  = loanReturnRepository
                .findLoanReturnById_RrOrderById_DateLoanDesc(operationDTO.getRfidReader()).isEmpty();

        if (!never_borrowed) {
            // verify if he is late
            isPunished = lateProxy.isPunished(operationDTO.getRfidReader());
            if (isPunished) {
                boolean rendred = loanReturnRepository.findLoanReturnById_RrOrderById_DateLoanDesc(operationDTO.getRfidReader())
                        .get(0).getId().getState() == BookState.RENDERING;
                if (rendred) return  punished_msg + new Date(
                        loanReturnRepository.findLoanReturnById_RrOrderById_DateLoanDesc(operationDTO.getRfidReader()).get(0).getDateReturn().getTime()+ punishment_days * DAY_MILLIS);
                // TODO send an email to the reponsible that here is the student with late
                return late_msg;
            }

            // verify if he already borrowing a book
            islender = loanReturnRepository.findLoanReturnById_RrOrderById_DateLoanDesc(operationDTO.getRfidReader())
                    .get(0).getId().getState() == BookState.BORROWED;
            if (islender) return already_borrowed;
        }

        Book book = booksProxy.getBook(operationDTO.getRfidBook(), "tocmd");
        rb = (book != null);
        if(!rb) return invalid_rb_msg;

        // never borrowed
        borrowed = !loanReturnRepository
                .findLoanReturnsById_RbOrderById_DateLoanDesc(operationDTO.getRfidBook()).isEmpty();

        if (!borrowed) {
            commandGateway.send(
                    new LoanCommand(
                            new KeyLoanReturn(operationDTO.getRfidReader(), operationDTO.getRfidBook(), new Date(), BookState.BORROWED),
                            reader, book, findDateReturn(new Date())));
            reserved = reservationProxy.verifyReservationDisponible(book.getIdNotice(), operationDTO.getRfidReader());
            if (reserved) {
                commandGateway.send(
                        new LoanCommand(new KeyLoanReturn(operationDTO.getRfidReader()
                                , operationDTO.getRfidBook(), new Date(), BookState.BORROWED), reader, book, findDateReturn(new Date())));
                reservationProxy.deleteReservation(book.getIdNotice(), operationDTO.getRfidReader());
                return ((never_borrowed) ? first_experience : "")
                        + loan_own_reserved;
            }
            reservationProxy.deleteReservation(book.getIdNotice(), operationDTO.getRfidReader());

            return ((never_borrowed)? first_experience:"")
                    + book_first_use +
                    valid_loan;
        }

        not_borrowed = loanReturnRepository
                .findLoanReturnsById_RbOrderById_DateLoanDesc
                        (operationDTO.getRfidBook())
                .get(0).getId().getState() != BookState.BORROWED;

        if (!not_borrowed) return loan_borrowed_book;

        long nbDispo = reservationProxy.countDisponible(book.getIdNotice());
        dispo = (nbDispo != 0 );
        if (!dispo) {
            reserved = reservationProxy.verifyReservationDisponible(book.getIdNotice(), operationDTO.getRfidReader());
            if (reserved) {
                commandGateway.send(
                        new LoanCommand(new KeyLoanReturn(operationDTO.getRfidReader()
                                , operationDTO.getRfidBook(), new Date(), BookState.BORROWED), reader, book,findDateReturn(new Date())));
                reservationProxy.deleteReservation(book.getIdNotice(), operationDTO.getRfidReader());
                return ((never_borrowed) ? first_experience : "")
                        + loan_own_reserved;
            }
            return reserved_msg;
        }

        commandGateway.send(
            new LoanCommand(
                    new KeyLoanReturn(operationDTO.getRfidReader(), operationDTO.getRfidBook(), new Date(), BookState.BORROWED),
                    reader, book, findDateReturn(new Date())));
        reservationProxy.deleteReservation(book.getIdNotice(), operationDTO.getRfidReader());

        return (never_borrowed)?first_experience:""
                +valid_loan;
    }

    @PostMapping("/return")
    public String returnOp(@RequestBody OperationDTO operationDTO){
        // starting to verify the existence of the RFIDs
        boolean rr = false;
        boolean borrowed = false;

        Reader reader = readerProxy.verifyRFIDReader(operationDTO.getRfidReader(),"toloan");

        rr = (reader != null);

        if(!rr) return invalid_rr_msg;

        borrowed = (!loanReturnRepository.findLoanReturnById_RrOrderById_DateLoanDesc(operationDTO.getRfidReader()).isEmpty()
                    && loanReturnRepository.findLoanReturnById_RrOrderById_DateLoanDesc(operationDTO.getRfidReader())
                .get(0).getId().getState() == BookState.BORROWED);
        if (!borrowed) return no_book_return;

        List<LoanReturn> loanReturns ;
        LoanReturn lastLoanReturn = null ;
        Date now = new Date();
        loanReturns = loanReturnRepository.findLoanReturnById_RrOrderById_DateLoanDesc(operationDTO.getRfidReader());
        lastLoanReturn = loanReturns.get(0);
        if (lastLoanReturn.getId().getRb().equals(operationDTO.getRfidBook())){
            if (now.before(lastLoanReturn.getDateReturn()) || now.equals(lastLoanReturn.getDateReturn())) {
                commandGateway.send(
                        new ReturnCommand(new KeyLoanReturn(operationDTO.getRfidReader(), operationDTO.getRfidBook(), lastLoanReturn.getId().getDateLoan(), BookState.RENDERING), new Date()));
                Long idNotice = booksProxy.getIdNotice(operationDTO.getRfidBook());
                Long waiting = reservationProxy.countWaiting(idNotice);
                if (waiting>0) {
                    reservationProxy.updateDispo(idNotice);
                }
                return valid_return;
            } else {
                commandGateway.send(
                        new ReturnCommand(new KeyLoanReturn(operationDTO.getRfidReader(), operationDTO.getRfidBook(), lastLoanReturn.getId().getDateLoan(), BookState.RENDERING), new Date()));
                lateProxy.punish(operationDTO.getRfidReader(),operationDTO.getRfidBook());
                return late_return;
            }
        } else return return_not_loaned;
    }

    @PostMapping("/prolongation")
    public  String prolongation(@RequestBody OperationDTO operationDTO)
    {
        boolean rr = false;
        boolean borrowed = false;

        Reader reader = readerProxy.verifyRFIDReader(operationDTO.getRfidReader(),"toloan");

        rr = (reader != null);

        if(!rr) return invalid_rr_msg;

        borrowed = (!loanReturnRepository.findLoanReturnById_RrOrderById_DateLoanDesc(operationDTO.getRfidReader()).isEmpty()
                && loanReturnRepository.findLoanReturnById_RrOrderById_DateLoanDesc(operationDTO.getRfidReader())
                .get(0).getId().getState() == BookState.BORROWED);
        if (!borrowed) return no_borrowed;


        List<LoanReturn> loanReturns ;
        LoanReturn lastLoanReturn = null ;
        Date now = new Date();
        loanReturns = loanReturnRepository.findLoanReturnById_RrOrderById_DateLoanDesc(operationDTO.getRfidReader());

        lastLoanReturn = loanReturns.get(0);
        Book book = booksProxy.getBook(lastLoanReturn.getId().getRb(),"tocmd");

        Long idnotice = booksProxy.getIdNotice(operationDTO.getRfidBook());
        book.setIdNotice(idnotice);

        Date temp = new Date(now.getTime() + prolong_valid_days * DAY_MILLIS);
        //   demander au ms- resv
        //  demander si il ya des reader en attente de ce livre
        long waiting = reservationProxy.countWaiting(idnotice);

        if (
                lastLoanReturn.getId().getState() == BookState.BORROWED
                        && lastLoanReturn.getDateReturn().after(now)
                        && (lastLoanReturn.getDateReturn().before(temp)) && (waiting == 0 )) {
            commandGateway.send(
                    new ReturnCommand(new KeyLoanReturn(operationDTO.getRfidReader(),lastLoanReturn.getId().getRb() , lastLoanReturn.getId().getDateLoan(), BookState.RENDERING), new Date()));

            commandGateway.send(
                    new LoanCommand(new KeyLoanReturn(operationDTO.getRfidReader(), lastLoanReturn.getId().getRb(), new Date(),BookState.BORROWED), reader, book,findDateReturn(new Date())));
            return valid_loan;
        } else {
            if ( waiting == 0 && now.before(new Date(lastLoanReturn.getDateReturn().getTime() - prolong_valid_days * DAY_MILLIS))) return "you cannot extend it now we attend some reservation you could reserve this book before 2 days of expiration\n starting from"+new Date(lastLoanReturn.getDateReturn().getTime() - prolong_valid_days * DAY_MILLIS);
            else
                return waiting_msg;
        }
    }

    @GetMapping("/verify")
    public boolean verifyOp(@RequestParam("op") String operation,@RequestParam("rr") String rr ){
        if(readerProxy.verifyRFIDReader(rr,"toloan") != null) {
            List<LoanReturn> loanReturns = loanReturnRepository.findLoanReturnById_RrOrderById_DateLoanDesc(rr);
            LoanReturn lastLoanReturn = null ;
            if(loanReturns != null && !loanReturns.isEmpty())  lastLoanReturn = loanReturns.get(0);
            switch (operation){
                case "loan":
                    return lastLoanReturn == null || lastLoanReturn.getId().getState() == BookState.RENDERING;
                case "return":
                    return lastLoanReturn != null && (lastLoanReturn.getId().getState() == BookState.BORROWED);
                /*case "reservation":
                    return lastLoanReturn == null || lastLoanReturn.getState() == BookState.RENDERING;*/
                default:
                    return  false;
            }
        }
        else return false;
    }

    @GetMapping("/loans")
    public List<LoanReturn> getloans(){
        return loanReturnRepository.findAll();
    }


    @GetMapping("/book/{rb}")
    public Book getbook(@PathVariable("rb") String rb){
        return booksProxy.getBook(rb,"tocmd");
    }


    public Date findDateReturn(Date dateLoan){
        Calendar c = Calendar.getInstance();
        c.setTime(dateLoan);
        c.add(Calendar.DATE, loan_period);
        Date dateReturn = c.getTime();
        while (!isOpen(dateReturn)){
            c.add(Calendar.DATE, 1);
            dateReturn = c.getTime();
        }
        return dateReturn;
    }

    public boolean isOpen(Date date){
        SimpleDateFormat day = new SimpleDateFormat("EEEE");
        SimpleDateFormat monDay = new SimpleDateFormat("MM-dd");
        String d = day.format(monDay);
        String md = monDay.format(date);
        return !weekend.contains(d) && !dates.contains(md);
    }
}