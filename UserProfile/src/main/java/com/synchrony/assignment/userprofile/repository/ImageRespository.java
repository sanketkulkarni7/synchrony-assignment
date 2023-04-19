package com.synchrony.assignment.userprofile.repository;

import com.synchrony.assignment.userprofile.model.Image;
import com.synchrony.assignment.userprofile.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * Spring DATA JPA repository for Image
 *
 * @author sanketku
 */

@Repository
public interface ImageRespository extends JpaRepository<Image, Long> {

    Set<Image> findByUser(User user);

    Image findByImageid(String imageid);


}
