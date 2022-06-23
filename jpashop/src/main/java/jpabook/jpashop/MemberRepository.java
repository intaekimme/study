package jpabook.jpashop;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class MemberRepository {

    @PersistenceContext
    private EntityManager em;

//    저장
    public Long save(Member member){
        em.persist(member);     //  커맨드와 쿼리 분리
        return member.getId();
    }

//    조회
    public Member find(Long id) {
        return em.find(Member.class, id);
    }
}
