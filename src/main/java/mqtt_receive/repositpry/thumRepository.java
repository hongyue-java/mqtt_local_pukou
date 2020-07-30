package mqtt_receive.repositpry;/*
package mqtt_receive.repositpry;

import mqtt_receive.entity.thumEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface thumRepository extends JpaRepository<thumEntity, Long> {
     @Transactional
     @Modifying
     @Query(value = "insert into THUM (?1) values (?2)",nativeQuery = true)
     void insertTHUM(String name,String val);

    //thumEntity

}
*/
