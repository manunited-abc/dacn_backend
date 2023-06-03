package nlu.dacn.dacn_backend.repository;

import nlu.dacn.dacn_backend.entity.Laptop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Repository
public class LaptopFilterRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public Page<Laptop> findLaptopByFilter(List<String> types, List<String> brands, List<String> chipCpus, int start, int limit) {
//      tạo CriteriaBuilder từ entityManager để xây dựng các truy vấn Criteria API.
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Laptop> criteriaQuery = criteriaBuilder.createQuery(Laptop.class);
        Root<Laptop> root = criteriaQuery.from(Laptop.class);
//      criteriaQuery.select(root) được sử dụng để chọn tất cả các cột trong bảng Laptop.
        criteriaQuery.select(root);

//      tạo danh sách các Predicate (điều kiện) để xác định các tiêu chí tìm kiếm.
        List<Predicate> predicates = new ArrayList<>();
        if (!types.isEmpty()) {
            predicates.add(root.get("type").in(types));
        }
        if (!brands.isEmpty()) {
            predicates.add(root.get("brand").in(brands));
        }
        if (!chipCpus.isEmpty()) {
            predicates.add(root.get("chipCpu").in(chipCpus));
        }
//      nếu danh sách predicates không rỗng
//      criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()])) để áp dụng các điều kiện tìm kiếm vào câu truy vấn.
        if (!predicates.isEmpty()) {
            criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()]));
        }

//      TypedQuery<Laptop> được tạo từ entityManager và câu truy vấn criteriaQuery.
        TypedQuery<Laptop> typedQuery = entityManager.createQuery(criteriaQuery);
//      typedQuery.setFirstResult(start * limit) được sử dụng để xác định vị trí bắt đầu của kết
//      quả truy vấn dựa trên trang và số lượng kết quả trên mỗi trang.
        typedQuery.setFirstResult(start * limit);
//      setMaxResults(limit) được sử dụng để giới hạn số lượng kết quả trả về trên mỗi trang.
        typedQuery.setMaxResults(limit);
//      truy vấn và lấy danh sách các đối tượng Laptop tương ứng từ cơ sở dữ liệu.
        List<Laptop> laptops = typedQuery.getResultList();

        while (start > 0) {
            if (laptops.size() > 0) {
                break;
            } else {
                start--;
                typedQuery.setFirstResult(start * limit);
                laptops = typedQuery.getResultList();
            }
        }

//      tạo câu truy vấn countQuery để đếm số lượng Laptop thỏa mãn các tiêu chí tìm kiếm.
//      sử dụng criteriaBuilder.count() để xác định số lượng kết quả.
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        countQuery.select(criteriaBuilder.count(countQuery.from(Laptop.class)));
//      nếu danh sách predicates không rỗng, sử dụng countQuery.where(predicates.toArray(new Predicate[predicates.size()]))
//      để áp dụng các điều kiện tìm kiếm vào câu truy vấn.
        if (!predicates.isEmpty()) {
            countQuery.where(predicates.toArray(new Predicate[predicates.size()]));
        }

//      thực hiện truy vấn và lấy kết quả đếm.
        Long count = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(laptops, PageRequest.of(start, limit), count);
    }
}
