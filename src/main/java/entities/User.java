package entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Entity(name = "User")
@Table(name = "User")

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    public int id;

    @Column(name = "name")
    public String name;

    @Column(name = "vorname")
    public String vorname;

    @Column(name = "userName", unique = true)
    public String userName;

    @Column(name = "hash")
    public String hash;

    @Column(name = "profilePicture")
    public byte[] image;
}
