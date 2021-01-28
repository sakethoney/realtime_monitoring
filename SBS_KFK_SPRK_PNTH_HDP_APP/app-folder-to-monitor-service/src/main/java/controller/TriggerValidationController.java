package controller;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ksh.crfi.app.zookeeper.dao.ZookeeperConfigurationManager;
import com.ksh.crfi.app.zookeeper.dao.constants.PropertyName;

import lombok.extern.log4j.Log4j;

@RestController
@Scope("session")
@Log4j
public class TriggerValidationController {

	@Autowired
	private ZookeeperConfigurationManager zkManager;
	
	@Autowired
	private ApplicationContext context;
	
	@Autowired
	private ProfileDBRepository profileDBRepository;
	
	@RequestMapping(value="/dovalidation/sourceforreview/{clientId}", method=RequestMethod.GET)
	@ResponseBody
	public void validateSourceForReveiw(@PathVariable String clientId) {
		log.info("DO DQ validation upon sfr manual mode"+clientId);
		String sourceForReveiw = String.format("%s\\%s\\source_for_review", zkManager.get(PropertyName.PENTAHO_APP_DATA_CLIENTS),clientId);
		Path sourceForReveiwPath = Paths.get(sourceForReveiw);
		List<FileProfile> profiles = profileDBRepository.findAllFilesProfile().stream()
				                     .filter(p-> p.getClientId().equals(clientId).filter(p-> new File(p.getFolderToMonitor()).exists())
				                     .filter(p-> {
				                    	 try {
				                    		 return Files.isSameFile(sourceForReveiwPath, Paths.get(p.getFolderToMonitor()));
				                    	 }catch(IOExceptions e) {
				                    		 log.error("Faild to compare folder to monitor and source for review path",e);
				                    		 return false;
				                    	 }
				                     }).collect(Collectors.toList());
	Montior monitor = new Monitor();
	monitor.setForManualMode(true);
	monitor.setProfiles(profiles);
	}
}
