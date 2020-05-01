package songbook.concert.rest;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import songbook.common.controller.BaseController;
import songbook.concert.entity.Concert;
import songbook.concert.entity.ConcertItem;
import songbook.concert.repository.ConcertDao;
import songbook.concert.repository.ConcertItemDao;
import songbook.concert.service.ConcertService;
import songbook.concert.service.ConcertServiceException;
import songbook.rest.model.Errors;
import songbook.song.repository.SongDao;
import songbook.song.service.SongService;
import songbook.user.entity.User;
import songbook.user.repository.UserDao;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import songbook.concert.view.Details;
import songbook.concert.view.Summary;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/concert")
public class ConcertController extends BaseController  {

    @Autowired
    private ConcertDao concertDao;

    @Autowired
    private ConcertItemDao concertItemDao;

    @Autowired
    ConcertService concertService;


    @Autowired
    private UserDao userDao;

    @Autowired
    private SongDao songDao;

    @Autowired
    private SongService songService;

    @GetMapping("{id}")
    @JsonView(Details.class)
    @ResponseBody
    public Concert findConcertById(@PathVariable("id") long id){
        initFilters();
        System.out.println(id);

        // + change on method findByIdAndUser
        return concertDao.findByIdWithHeadersAndTags(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "item not found"));
    }

    @GetMapping("last")
    @JsonView(Details.class)
    @ResponseBody
    public Concert findLastConcert(){
        initFilters();
        Pageable lastRecordPage = PageRequest.of(0, 1);

        List<Concert> concerts = concertDao.findLastConcertWithHeadersAndTags(lastRecordPage);

        if(concerts.size() > 0) {
            return concerts.get(0);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "last concert item could not be found");
        }
    }

    @GetMapping("")
    @JsonView(Summary.class)
    public Page<Concert> findAll(Pageable pageable){
        initFilters();
        // change on method findAllByUser + default sorting
        return concertDao.findAll(pageable);
    }

    @PostMapping("item")
    @JsonView(Details.class)
    public Object createConcertItem(@RequestBody @Valid ConcertItem concertItem, BindingResult bindingResult) throws ResponseStatusException {

        if (bindingResult.hasErrors()) {
            Errors errors = new Errors("Bad request!");
            errors.convertFieldErrors(bindingResult.getFieldErrors());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);

        } else {

            // auto-assign order-value
            // todo: this -> into service
            if (concertItem.getOrderValue() == 0) {
                try {
                    this.concertService.setNewConcertItemOrderValue(concertItem);
                } catch(ConcertServiceException e) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "An error has occurred while setting the new item's orderValue");
                }
            }

            return concertItemDao.save(concertItem);
        }
    }

    // When a client needs to replace an existing Resource entirely, they can use PUT. When they're doing a partial update, they can use HTTP PATCH.
    @PutMapping("item/bulk")
    public List<ConcertItem> updateConcertItemsBulk(@RequestBody List<ConcertItem> items) throws ResponseStatusException {

        System.out.println(items.size() + ": the size");

        // auto assign order-values
        // todo: this -> into service
        concertService.setConcertItemsOrderValues(items);

        return concertItemDao.saveAll(items);
    }

    @PostMapping("")
    @JsonView(Details.class)
    public Object createConcert(@RequestBody @Valid Concert concert, BindingResult bindingResult) throws ResponseStatusException {

        if (bindingResult.hasErrors()) {
            Errors errors = new Errors("Bad request!");
            errors.convertFieldErrors(bindingResult.getFieldErrors());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        } else {

            return concertDao.save(concert);
        }
    }

    @DeleteMapping("item/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable("id") long id) {
        ConcertItem concertItem = concertItemDao.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "concert item not found"));
        concertItemDao.delete(concertItem);

        Map<String, String> response = new HashMap<>();
        response.put("result", "ok");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    private void initFilters() {
        User user = getDefaultUser();
        songDao.initContentUserFilter(user);
    }
}
