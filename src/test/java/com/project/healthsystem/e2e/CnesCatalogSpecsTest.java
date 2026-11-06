package com.project.healthsystem.e2e;

import com.project.healthsystem.model.datasus_data.cnes.*;
import com.project.healthsystem.repository.specs.*;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class CnesCatalogSpecsTest {
    @Test void professionalFiltersAreCombinedAndCodesStayStrings() {
        Root<CnesProfessional> root = mock(Root.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        Path<String> name = mock(Path.class), cns = mock(Path.class), cnes = mock(Path.class), cbo = mock(Path.class);
        Expression<String> lowerName = mock(Expression.class), lowerCns = mock(Expression.class), upperCbo = mock(Expression.class);
        when(root.<String>get("name")).thenReturn(name); when(root.<String>get("cns")).thenReturn(cns);
        when(root.<String>get("cnes")).thenReturn(cnes); when(root.<String>get("cbo")).thenReturn(cbo);
        when(cb.lower(name)).thenReturn(lowerName); when(cb.lower(cns)).thenReturn(lowerCns);
        when(cb.upper(cbo)).thenReturn(upperCbo);
        Predicate initial=mock(Predicate.class), byName=mock(Predicate.class), byCns=mock(Predicate.class),
            search=mock(Predicate.class), searched=mock(Predicate.class), byCnes=mock(Predicate.class),
            filtered=mock(Predicate.class), byCbo=mock(Predicate.class), result=mock(Predicate.class);
        when(cb.conjunction()).thenReturn(initial);
        when(cb.like(lowerName,"%ana!%!_!!%",'!')).thenReturn(byName);
        when(cb.like(lowerCns,"%ana!%!_!!%",'!')).thenReturn(byCns);
        when(cb.or(byName,byCns)).thenReturn(search);
        when(cb.and(initial,search)).thenReturn(searched);
        when(cb.equal(cnes,"0000123")).thenReturn(byCnes);
        when(cb.and(searched,byCnes)).thenReturn(filtered);
        when(cb.equal(upperCbo,"2231F9")).thenReturn(byCbo);
        when(cb.and(filtered,byCbo)).thenReturn(result);
        assertSame(result, CnesProfessionalSpecs.search(" Ana%_! "," 0000123 "," 2231f9 ").toPredicate(root,query,cb));
        verify(cb).equal(cnes,"0000123"); verify(cb).equal(upperCbo,"2231F9");
    }

    @Test void emptyProfessionalFiltersDoNotRestrictCatalog() {
        Root<CnesProfessional> root=mock(Root.class);
        CriteriaBuilder cb=mock(CriteriaBuilder.class);
        Predicate all=mock(Predicate.class); when(cb.conjunction()).thenReturn(all);
        assertSame(all,CnesProfessionalSpecs.search(" ",null," ").toPredicate(root,mock(CriteriaQuery.class),cb));
        verifyNoInteractions(root);
    }

    @Test void establishmentSearchUsesAllFourFieldsWithLiteralWildcards() {
        Root<Estabilishment> root=mock(Root.class);
        CriteriaBuilder cb=mock(CriteriaBuilder.class);
        Predicate initial=mock(Predicate.class); when(cb.conjunction()).thenReturn(initial);
        for(String field : new String[]{"legalName","tradeName","cnesCode","cnpj"}) {
            Path<String> path=mock(Path.class); Expression<String> lower=mock(Expression.class);
            when(root.<String>get(field)).thenReturn(path); when(cb.lower(path)).thenReturn(lower);
        }
        CnesEstabilishmentSpecs.search(" UNIDADE%_! ").toPredicate(root,mock(CriteriaQuery.class),cb);
        for(String field : new String[]{"legalName","tradeName","cnesCode","cnpj"})
            verify(cb).like(cb.lower(root.get(field)),"%unidade!%!_!!%",'!');
        verify(cb).or(any(Predicate[].class));
    }

    @Test void emptyEstablishmentSearchDoesNotRestrictCatalog() {
        Root<Estabilishment> root=mock(Root.class);
        CriteriaBuilder cb=mock(CriteriaBuilder.class);
        Predicate all=mock(Predicate.class); when(cb.conjunction()).thenReturn(all);
        assertSame(all,CnesEstabilishmentSpecs.search(null).toPredicate(root,mock(CriteriaQuery.class),cb));
        verifyNoInteractions(root);
    }
}
