package entities;

import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name = "User")
@Table(name = "User")

@Data
public class User {
    public User(){

    }

    @Id
    @Column(name = "id")
    public int id;

    @Column(name = "name")
    public String name;

    @Column(name = "vorname")
    public String vorname;

    @Column(name = "userName")
    public String userName;

    @Column(name = "hash")
    public String hash;

    @Column(name = "profilePicture")
    public byte[] image;
}
