package org.yearup.data;


import org.yearup.models.Profile;

import java.util.List;

public interface ProfileDao
{
    Profile create(Profile profile);

    //added additional methods for my profile
    List<Profile> getAllProfile();
    Profile getByUserId(int id);
    void update(int userId,Profile profile);
}
