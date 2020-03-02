package songbook.rest.util;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class SearchQueryCriteriaConsumer<T> implements Consumer<SearchCriteria>, Specification<T> {

    private List<SearchCriteria> criteria;

    public SearchQueryCriteriaConsumer() {
        super();
        this.criteria = new ArrayList<SearchCriteria>();
    }

    @Override
    public void accept(SearchCriteria param) {
        this.criteria.add(param);
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

        List<Predicate> predicates = new ArrayList<>();

        for(SearchCriteria param : criteria){

            System.out.println(param.getOperation() + " is operation, " + param.getKey() + " is key\n");

            if (param.getOperation().equals(">")) {
                predicates.add(builder.greaterThan(root.get(param.getKey()), param.getValue().toString()));

            } else if (param.getOperation().equals("<")) {
                predicates.add(builder.lessThan(root.get(param.getKey()), param.getValue().toString()));

            } else if (param.getOperation().equals("<=")) {
                predicates.add(builder.lessThanOrEqualTo(root.get(param.getKey()), param.getValue().toString()));

            } else if (param.getOperation().equals(">=")) {
                predicates.add(builder.greaterThanOrEqualTo(root.get(param.getKey()), param.getValue().toString()));

            } else if (param.getOperation().equalsIgnoreCase("in") || param.getOperation().equalsIgnoreCase("nin")) {
                Expression<String> keyExpr = root.get(param.getKey());
                List<String> values = Arrays.asList(param.getValue().toString().split(","));

                if(param.getOperation().equalsIgnoreCase("nin")){
                    predicates.add(builder.not(keyExpr.in(values)));
                } else {
                    predicates.add(builder.equal(root.get(param.getKey()), param.getValue().toString()));
                }
            } else if (param.getOperation().equalsIgnoreCase("nlike")) {
                predicates.add(builder.notLike(root.get(param.getKey()), "%" + param.getValue().toString() + "%"));

            } else if (param.getOperation().equalsIgnoreCase("like")) {
                System.out.println("like: " + "%" + param.getValue().toString() + "%");
                predicates.add(builder.like(root.get(param.getKey()), "%" + param.getValue().toString() + "%"));

            } else if (param.getOperation().equalsIgnoreCase("neq")) {
                predicates.add(builder.notEqual(root.get(param.getKey()), param.getValue().toString()));

            } else if (param.getOperation().equals(":") || param.getOperation().equalsIgnoreCase("eq")) {
                predicates.add(builder.equal(root.get(param.getKey()), param.getValue().toString()));
            }
        }

        Predicate[] arr = new Predicate[predicates.size()];
        return builder.and(predicates.toArray(arr));
    }
}