/*
 * Licensed to the University of California, Berkeley under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package tachyon.worker;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

import tachyon.Constants;
import tachyon.worker.block.BlockLock;

/**
 * Handle all block locks.
 * <p>
 * This class is thread-safe as all public methods are syncrhonized.
 */
public class BlockLockManager {
  private static final Logger LOG = LoggerFactory.getLogger(Constants.LOGGER_TYPE);

  /** A map from a block ID to its lock **/
  private final Map<Long, BlockLock> mBlockIdToLockMap = new HashMap<Long, BlockLock>();

  public BlockLockManager() {}

  /**
   * Get the lock for the given block id. If there is no such a lock yet, create one.
   *
   * @param blockId The id of the block.
   * @return the lock for this block or absent.
   */
  public synchronized Optional<BlockLock> getBlockLock(long blockId) {
    if (!mBlockIdToLockMap.containsKey(blockId)) {
      LOG.error("Cannot get lock for block {}: not exists", blockId);
      return Optional.absent();
    }
    return Optional.of(mBlockIdToLockMap.get(blockId));
  }

  public synchronized boolean addBlockLock(long blockId) {
    if (mBlockIdToLockMap.containsKey(blockId)) {
      LOG.error("Cannot add lock for block {}: already exists", blockId);
      return false;
    }
    mBlockIdToLockMap.put(blockId, new BlockLock(blockId));
    return true;
  }

  /**
   * Remove a lock for the given block id.
   *
   * @param blockId The id of the block.
   * @return the lock removed
   */
  public synchronized boolean removeBlockLock(long blockId) {
    if (!mBlockIdToLockMap.containsKey(blockId)) {
      LOG.error("Cannot remove lock for block {}: not exists", blockId);
      return false;
    }
    mBlockIdToLockMap.remove(blockId);
    return true;
  }
}