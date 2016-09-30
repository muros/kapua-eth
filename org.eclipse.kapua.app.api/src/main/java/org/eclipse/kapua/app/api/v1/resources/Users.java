/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates and others
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eurotech - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.kapua.app.api.v1.resources;

//
//@Path("/users")
//public class Users extends AbstractKapuaResource 
//{
//	private final KapuaLocator locator = KapuaLocator.getInstance();
//	
//	/**
//	 * Returns the list of all the users associated to the account of the
//	 * currently connected user.
//	 *
//	 * @return The list of requested User objects.
//	 */
////	@GET
////	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
////	public UserListResult getUsers() {
////		
////		UserFactory userFactory = locator.getFactory(UserFactory.class);
////		UserListResult usersResult = userFactory.newUserListResult();
////		
////		try {
////			
////			UserService us = locator.getService(UserService.class);
////
////			KapuaId targetAccountId = getTargetAccountId();
////
////			UserQuery queryByScopeId = userFactory.newQuery(targetAccountId);
////			usersResult = us.query(queryByScopeId);
////
////		} catch (Throwable t) {
////			handleException(t);
////		}
////		return usersResult;
////	}
//
//	/**
//	 * Returns the User specified by the "id" path parameter.
//	 *
//	 * @param userId
//	 *            The id of the User requested.
//	 * @return The requested User object.
//	 */
////	@GET
////	@Path("{userId}")
////	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
////	public User getUser(@PathParam("userId") long userId) {
////		User user = null;
////		try {
////			ServiceLocator sl = ServiceLocator.getInstance();
////			UserService as = sl.getUserService();
////
////			long targetAccountId = getTargetAccountId();
////
////			user = as.findWithAccessInfo(targetAccountId, userId);
////		} catch (Throwable t) {
////			handleException(t);
////		}
////		return returnNotNullEntity(user);
////	}
//
//	/**
//	 * Returns the User specified by the "username" query parameter.
//	 *
//	 * @param username
//	 *            The username of the User requested.
//	 * @return The requested User object.
//	 */
////	@GET
////	@Path("findByName")
////	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
////	public User getUserByName(@QueryParam("username") String username) {
////		User user = null;
////		try {
////			ServiceLocator sl = ServiceLocator.getInstance();
////			UserService as = sl.getUserService();
////
////			long targetAccountId = getTargetAccountId();
////
////			user = as.findByNameWithAccessInfo(targetAccountId, username);
////		} catch (Throwable t) {
////			handleException(t);
////		}
////		return returnNotNullEntity(user);
////	}
//
//	/**
//	 * Unlock a User based on the userId provided in the path request.
//	 *
//	 * @param userId
//	 *            The userId that refer to the user to unlock.
//	 *
//	 * @return The user unlocked.
//	 */
////	@POST
////	@Path("{userId}/unlock")
////	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
////	public User unlockUser(@PathParam("userId") long userId) {
////		User user = null;
////
////		try {
////			long targetAccountId = getTargetAccountId();
////
////			ServiceLocator sl = ServiceLocator.getInstance();
////			UserService us = sl.getUserService();
////			user = us.unlockUser(targetAccountId, userId);
////			user = us.findWithAccessInfo(targetAccountId, userId);
////		} catch (Throwable t) {
////			handleException(t);
////		}
////		return returnNotNullEntity(user);
////	}
//
//	/**
//	 * Creates a new User based on the information provided in UserCreator
//	 * parameter.
//	 *
//	 * @param userCreator
//	 *            Provides the information for the new User to be created.
//	 * @return The newly created User object.
//	 */
////	@POST
////	@Consumes(MediaType.APPLICATION_XML)
////	@Produces(MediaType.APPLICATION_XML)
////	public User postUser(UserCreator userCreator) {
////		User user = null;
////		try {
////			ServiceLocator sl = ServiceLocator.getInstance();
////			UserService us = sl.getUserService();
////			user = us.create(userCreator);
////			user = us.findWithAccessInfo(user.getAccountId(), user.getId());
////		} catch (Throwable t) {
////			handleException(t);
////		}
////		return returnNotNullEntity(user);
////	}
//
//	/**
//	 * Updates the User specified by the "id" path parameter based on the
//	 * information provided in the User parameter.
//	 *
//	 * @param userId
//	 *            The id of the User to be updated.
//	 * @param account
//	 *            The modified User whose attributed need to be updated.
//	 * @return The updated user.
//	 */
////	@PUT
////	@Path("{userId}")
////	@Consumes(MediaType.APPLICATION_XML)
////	@Produces(MediaType.APPLICATION_XML)
////	public User putUser(@PathParam("userId") String userId, User user) {
////		User userUpdated = null;
////		try {
////			ServiceLocator sl = ServiceLocator.getInstance();
////			UserService us = sl.getUserService();
////			userUpdated = us.update(user);
////			userUpdated = us.findWithAccessInfo(user.getAccountId(), user.getId());
////		} catch (Throwable t) {
////			handleException(t);
////		}
////		return returnNotNullEntity(userUpdated);
////	}
//
//	/**
//	 * Deletes the User specified by the "id" path parameter.
//	 *
//	 * @param userId
//	 *            The id of the User to be deleted.
//	 */
////	@DELETE
////	@Path("{userId}")
////	public void deleteUser(@PathParam("userId") long userId) {
////		try {
////			ServiceLocator sl = ServiceLocator.getInstance();
////			UserService us = sl.getUserService();
////
////			long targetAccountId = getTargetAccountId();
////
////			us.delete(targetAccountId, userId);
////		} catch (Throwable t) {
////			handleException(t);
////		}
////	}
//}
