package songbook.tag.rest;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import songbook.rest.util.SearchCriteria;
import songbook.rest.util.SearchQueryCriteriaConsumer;
import songbook.tag.entity.Tag;
import songbook.tag.repository.TagDao;
import songbook.tag.view.Summary;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/tag")
public class TagController {

    @Autowired
    private TagDao tagDao;

    @GetMapping("{id}")
    @ResponseBody
    public Tag getTagById(@PathVariable("id") long id){
        System.out.println(id);
        return tagDao.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "item not found"));
    }

    // now this supports search=title:Zzz,id>2
    // i want to do something like this: [{"title": "aaa"},{"id": {"in":[1,2,3]}] with json-simple
    // or filter=title:aaa;order>3;id:1,2,3:nin
    @GetMapping("")
    @JsonView(Summary.class)
    public Page<Tag> findTags(@RequestParam(required = false, name = "filter") String filter, Pageable pageable){

        List<SearchCriteria> params = new ArrayList<SearchCriteria>();
        if(filter != null) {
            Pattern pattern = Pattern.compile("(\\w+?)(:|<|<=|>|>=)([\\w,]+?)(?:(:)(\\w+))?;");
            Matcher matcher = pattern.matcher(filter + ";");
            while (matcher.find()) {
                if(matcher.group(5) != null) {
                    params.add(new SearchCriteria(matcher.group(1),matcher.group(5), matcher.group(3)));
                } else {
                    params.add(new SearchCriteria(matcher.group(1),matcher.group(2), matcher.group(3)));
                }
            }

            SearchQueryCriteriaConsumer<Tag> searchConsumer = new SearchQueryCriteriaConsumer<Tag>();
            params.stream().forEach(searchConsumer);
            Page<Tag> result = tagDao.findAll(searchConsumer, pageable);
            System.out.println(result.getTotalElements() + " found");
            return result;
        } else {
            return tagDao.findAll(pageable);
        }
    }

    @PostMapping("")
    public Tag createTag(@RequestBody Tag newTag){

        List<Tag> foundTags = tagDao.findByTitleAllIgnoreCase(newTag.getTitle());

        if (foundTags.size() > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Such tag is already exist");
        } else {
            return tagDao.save(newTag);
        }
    }
}
