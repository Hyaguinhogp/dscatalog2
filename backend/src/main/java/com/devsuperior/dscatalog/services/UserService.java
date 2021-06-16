package com.devsuperior.dscatalog.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.RoleDTO;
import com.devsuperior.dscatalog.dto.UserDTO;
import com.devsuperior.dscatalog.dto.UserInsertDTO;
import com.devsuperior.dscatalog.dto.UserUpdateDTO;
import com.devsuperior.dscatalog.entities.User;
import com.devsuperior.dscatalog.repositories.RoleRepository;
import com.devsuperior.dscatalog.repositories.UserRepository;
import com.devsuperior.dscatalog.services.exceptions.ResourcesNotFoundException;

@Service
public class UserService implements UserDetailsService{
	
	private static Logger logger = org.slf4j.LoggerFactory.getLogger(UserService.class);
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository repository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Transactional(readOnly = true)
	public List<UserDTO> findAll() {
		List<UserDTO> list = repository.findAll().stream().map(x -> new UserDTO(x)).collect(Collectors.toList());
		return list;
	}
	
	@Transactional(readOnly = true)
	public Page<UserDTO> findAllPaged(Pageable pageable){
		Page<User> page = repository.findAll(pageable);
		return page.map(x -> new UserDTO(x));
	}
	
	@Transactional(readOnly = true)
	public UserDTO findById(long id) {
		Optional<User> obj = repository.findById(id);
		User user = obj.orElseThrow(() -> new ResourcesNotFoundException("Entity not found"));
		return new UserDTO(user);
	}
	
	@Transactional
	public UserDTO insert(UserInsertDTO dto) {
		User user = new User();
		dtoToUser(dto, user);
		user.setPassword(passwordEncoder.encode(dto.getPassword()));
		repository.save(user);
		return new UserDTO(user);
		
	}

	@Transactional
	public UserDTO update(long id, UserUpdateDTO dto) {
		User user = new User();
		dtoToUser(dto, user);
		user.setId(id);
		repository.save(user);
		return new UserDTO(user);
	}
	
	@Transactional
	public void delete(long id) {
		repository.deleteById(id);

	}
	
	@Transactional
	
	private void dtoToUser(UserDTO dto, User user) {
		user.setFirstName(dto.getFirstName());
		user.setLastName(dto.getLastName());
		user.setEmail(dto.getEmail());
		
		user.getRoles().clear();
		for(RoleDTO entity : dto.getRoles()) {
			user.getRoles().add(roleRepository.getOne(entity.getId()));
		}
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = repository.findByEmail(username);
		
		if(user == null) {
			logger.error("User not found: " + username);
			throw new UsernameNotFoundException("Email not found");
		}
		logger.info("User found: " + username);
		return user;
	}
}
