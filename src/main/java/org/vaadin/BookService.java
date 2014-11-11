/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.vaadin;

import org.vaadin.entities.Book;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

@Stateless
public class BookService {

    @PersistenceContext(unitName = "book-pu")
    private EntityManager entityManager;

    public void saveOrPersist(Book book)
    {
        book.setBookTitle(book.getBookTitle().trim());
        if(book.getBookId() > 0) {
            entityManager.merge(book);
        } else {
            entityManager.persist(book);
        }
    }

    public List<Book> getAllBooks()
    {
        CriteriaQuery<Book> cq = entityManager.getCriteriaBuilder().createQuery(Book.class);
        cq.select(cq.from(Book.class));
        return entityManager.createQuery(cq).getResultList();
    }
    
    public void deleteBook(Book book) {
        if(book.getBookId() > 0) {
            // reattach to remove
            book = entityManager.merge(book);
            entityManager.remove(book);
        }
    }
    
    public Book findById(int bookId) {
        return entityManager.find(Book.class, bookId);
    }
    
    public List<Book> findByTitle(String bookTitle) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> q = cb.createQuery(Book.class);
        Root<Book> r = q.from(Book.class);
        CriteriaQuery<Book> cq = q.select(r).where(cb.equal(r.get("bookTitle"), bookTitle));
        return entityManager.createQuery(cq).getResultList();
    }
}
